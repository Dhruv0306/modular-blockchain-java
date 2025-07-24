async function loadBlockchain() {
  const response = await fetch('/api/chain');
  const chain = await response.json();

  const container = document.getElementById('blockchain');
  container.innerHTML = '';

  chain.forEach(block => {
    const blockEl = document.createElement('div');
    blockEl.className = 'block';

    blockEl.innerHTML = `
      <h3>📦 Block #${block.index}</h3>
      <p><strong>Hash:</strong> ${block.hash}</p>
      <p><strong>PrevHash:</strong> ${block.previousHash}</p>
      <p><strong>Nonce:</strong> ${block.nonce}</p>
      <p><strong>Timestamp:</strong> ${new Date(block.timestamp).toLocaleString()}</p>
      <p><strong>Transactions:</strong></p>
      <div class="tx">${renderTransactions(block.transactions)}</div>
    `;

    container.appendChild(blockEl);
  });
}

function renderTransactions(txs) {
  if (!txs || txs.length === 0) {
    return '<div class="no-tx">No transactions</div>';
  }

  return txs.map(tx => {
    const summary = tx.amount !== undefined
      ? `${tx.sender} → ${tx.receiver} | ₹${tx.amount}`
      : tx.getSummary ?? JSON.stringify(tx);
    return `<div>${summary}</div>`;
  }).join('');
}

window.onload = loadBlockchain;

// Auto-refresh every 10 seconds
setInterval(() => {
  loadBlockchain();
}, 10000);