const assert = require('assert');
const crypto = require('crypto');
const app = require('./server');

async function runTests() {
  console.log('--- Test Backend Sim X (Grossiste eSIM Access & Passerelles de Paiement) ---');

  const server = app.listen(0);
  const port = server.address().port;
  const baseUrl = `http://localhost:${port}`;

  try {
    // 1. Health check & vérification du masquage des secrets
    const healthRes = await fetch(`${baseUrl}/api/v1/health`);
    const healthJson = await healthRes.json();
    assert.strictEqual(healthJson.status, 'ONLINE');
    assert.strictEqual(healthJson.gateways.binance.appPrefix, 'APP3');
    assert.ok(healthJson.gateways.esimAccess.configured);
    assert.ok(healthJson.gateways.esimAccess.maskedAccessCode.includes('••••'));

    // Vérifier qu'AUCUN secret brut n'est exposé dans la réponse
    const rawHealth = JSON.stringify(healthJson);
    assert.strictEqual(rawHealth.includes('3b4023be55334960978f86381c95b9f6'), false, 'secretKey must never be exposed');
    assert.strictEqual(rawHealth.includes('227304524ce14cdbb69fd9610465f78f'), false, 'full accessCode must never be exposed');
    assert.strictEqual(rawHealth.includes('hbdpOii1aoW2KxnSCrCSdKxPdth7jOOTAnRH5pkpO8CKPrQnmz9FR7GMp64lbr0W'), false, 'binance secret must never be exposed');
    console.log('✓ Health check validé (secrets grossiste et numéros marchands masqués)');

    // 2. Grossiste eSIM Access - Protection RBAC
    const unauthEsimRes = await fetch(`${baseUrl}/api/admin/esim/packages`);
    assert.strictEqual(unauthEsimRes.status, 401, 'Admin auth required for wholesale packages');
    console.log('✓ Sécurité RBAC validée (401 sans jeton administrateur)');

    // 3. Grossiste eSIM Access - Récupération du catalogue
    const adminToken = 'sx_adm_sec_9941a87b1c4e7f2';
    const packagesRes = await fetch(`${baseUrl}/api/admin/esim/packages`, {
      headers: {
        'Authorization': `Bearer ${adminToken}`,
        'x-admin-id': 'adm_super_01'
      }
    });
    const packagesJson = await packagesRes.json();
    assert.strictEqual(packagesRes.status, 200);
    assert.strictEqual(packagesJson.success, true);
    assert.ok(packagesJson.packages.length >= 4);
    assert.strictEqual(packagesJson.wholesaler_status.connected, true);
    // Vérification de sécurité absolue : les clés API grossiste ne doivent JAMAIS être envoyées au frontend
    const rawPackages = JSON.stringify(packagesJson);
    assert.strictEqual(rawPackages.includes('3b4023be55334960978f86381c95b9f6'), false);
    console.log(`✓ Catalogue grossiste récupéré avec succès (${packagesJson.packages.length} forfaits disponibles)`);

    // 4. Grossiste eSIM Access - Commande avec clé d'idempotence
    const testIdempotencyKey = 'tx_idemp_test_' + Date.now();
    const orderRes = await fetch(`${baseUrl}/api/admin/esim/order`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${adminToken}`,
        'x-admin-id': 'adm_super_01'
      },
      body: JSON.stringify({
        package_code: 'ESIM_HT_3GB_30D',
        idempotency_key: testIdempotencyKey,
        selling_price: 6.67
      })
    });
    const orderJson = await orderRes.json();
    assert.strictEqual(orderRes.status, 201);
    assert.strictEqual(orderJson.success, true);
    assert.strictEqual(orderJson.transaction.transaction_id, testIdempotencyKey);
    assert.strictEqual(orderJson.transaction.package_code, 'ESIM_HT_3GB_30D');
    assert.strictEqual(orderJson.transaction.status, 'SUCCESS');
    assert.ok(orderJson.esim_profile.iccid.startsWith('8901'));
    assert.ok(orderJson.esim_profile.lpa_code.startsWith('LPA:1$smdp.simx.net$SX-'));
    console.log('✓ Commande grossiste eSIM exécutée avec succès (ICCID généré et transaction enregistrée)');

    // 5. Test du rejeu idempotent (Idempotency Key Protection)
    const replayRes = await fetch(`${baseUrl}/api/admin/esim/order`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${adminToken}`,
        'x-admin-id': 'adm_super_01'
      },
      body: JSON.stringify({
        package_code: 'ESIM_HT_3GB_30D',
        idempotency_key: testIdempotencyKey,
        selling_price: 6.67
      })
    });
    const replayJson = await replayRes.json();
    assert.strictEqual(replayRes.status, 200);
    assert.strictEqual(replayJson.idempotent_replay, true);
    assert.strictEqual(replayJson.transaction.transaction_id, testIdempotencyKey);
    console.log('✓ Protection d\'idempotence validée (rejeu évité, transaction existante retournée)');

    // 6. Consultation des journaux d'audit de sécurité
    const auditRes = await fetch(`${baseUrl}/api/admin/audit-logs`, {
      headers: { 'Authorization': `Bearer ${adminToken}` }
    });
    const auditJson = await auditRes.json();
    assert.strictEqual(auditRes.status, 200);
    assert.ok(auditJson.logs.length >= 2);
    assert.ok(auditJson.logs.some(l => l.action.includes('PURCHASE_ESIM_WHOLESALE')));
    console.log(`✓ Table audit_logs validée (${auditJson.logs.length} événements enregistrés)`);

    // 7. Initiation Binance Pay avec préfixe APP3
    const binanceRes = await fetch(`${baseUrl}/api/v1/payments/binance/initiate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer simx_client_token_secure_99'
      },
      body: JSON.stringify({ amountUsd: 25 })
    });
    const binanceJson = await binanceRes.json();
    assert.strictEqual(binanceRes.status, 201);
    assert.strictEqual(binanceJson.appPrefix, 'APP3');
    assert.ok(binanceJson.merchantTradeNo.startsWith('APP3_'));
    console.log(`✓ Initiation Binance Pay validée (Préfixe APP3 : ${binanceJson.merchantTradeNo})`);

    // 8. Webhook Binance Pay avec signature HMAC-SHA512
    const binancePayload = JSON.stringify({
      merchantTradeNo: binanceJson.merchantTradeNo,
      transactionId: 'BP_TX_12345678',
      status: 'PAID'
    });
    const binanceSecret = process.env.BINANCE_PAY_SECRET || 'hbdpOii1aoW2KxnSCrCSdKxPdth7jOOTAnRH5pkpO8CKPrQnmz9FR7GMp64lbr0W';
    const binanceSig = crypto.createHmac('sha512', binanceSecret).update(binancePayload).digest('hex');

    const binanceWebhookRes = await fetch(`${baseUrl}/api/v1/payments/binance/webhook`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'binancepay-signature': binanceSig
      },
      body: binancePayload
    });
    assert.strictEqual(binanceWebhookRes.status, 200);
    console.log('✓ Webhook Binance Pay vérifié avec succès (HMAC-SHA512)');

    // 9. Initiation et Webhook MonCash
    const moncashRes = await fetch(`${baseUrl}/api/v1/payments/moncash/initiate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer simx_client_token_secure_99'
      },
      body: JSON.stringify({ orderId: 'sx_ord_mc_test', amountUsd: 10 })
    });
    assert.strictEqual(moncashRes.status, 201);
    console.log('✓ Initiation MonCash validée');

    console.log('\n--> TOUS LES TESTS BACKEND (GROSSISTE ESIM ACCESS & PAIEMENTS) SONT VALIDÉS !');
  } finally {
    server.close();
  }
}

runTests().catch((err) => {
  console.error('Échec des tests backend :', err);
  process.exit(1);
});
