async function loadBlockDetail() {
  const urlParams = new URLSearchParams(window.location.search);
  const blockId = urlParams.get('id');
  
  if (!blockId) {
    document.getElementById('block-detail').innerHTML = '<p>No block ID specified.</p>';
    return;
  }

  try {
    const response = await fetch(`/api/block/${blockId}`);
    if (!response.ok) {
      document.getElementById('block-detail').innerHTML = '<p>Block not found.</p>';
      return;
    }
    
    const block = await response.json();
    renderBlockDetail(block);
  } catch (error) {
    document.getElementById('block-detail').innerHTML = '<p>Error loading block details.</p>';
  }
}

function renderBlockDetail(block) {
  const container = document.getElementById('block-detail');
  
  container.innerHTML = `
    <div class="block">
      <h3>📦 Block #${block.index}</h3>
      <p><strong>Hash:</strong> ${block.hash}</p>
      <p><strong>Previous Hash:</strong> ${block.previousHash}</p>
      <p><strong>Nonce:</strong> ${block.nonce}</p>
      <p><strong>Timestamp:</strong> ${new Date(block.timestamp).toLocaleString()}</p>
      <p><strong>Transactions (${block.transactions.length}):</strong></p>
      <div class="tx">${renderTransactions(block.transactions)}</div>
    </div>
  `;
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

function toggleTheme() {
  document.body.classList.toggle("dark-mode");
}

function goBack() {
  window.location.href = '/explorer';
}

window.onload = loadBlockDetail;