let fullChain = [];

async function loadBlockchain() {
  const response = await fetch('/api/chain');
  fullChain = await response.json();
  renderBlocks(fullChain);
}

function renderBlocks(chain) {
  const container = document.getElementById('blockchain');
  
  if (chain.length === 0) {
    container.innerHTML = '<p>No blocks found.</p>';
    return;
  }

  container.innerHTML = `
    <table>
      <thead>
        <tr>
          <th>Block</th>
          <th>Hash</th>
          <th>Transactions</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        ${chain.map(block => `
          <tr>
            <td>Block #${block.index}</td>
            <td>${block.hash.substring(0, 16)}...</td>
            <td>${block.transactions.length} transaction(s)</td>
            <td><button class="view-btn" onclick="viewBlock(${block.index})">View Details</button></td>
          </tr>
        `).join('')}
      </tbody>
    </table>
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

window.onload = () => {
  loadBlockchain();
  
  // Add search functionality
  document.getElementById("search").addEventListener("input", (e) => {
    const value = e.target.value.toLowerCase();
    const filtered = fullChain.filter(block =>
      block.hash.toLowerCase().includes(value) ||
      block.index.toString() === value ||
      block.transactions.some(tx =>
        tx.sender?.toLowerCase().includes(value) ||
        tx.receiver?.toLowerCase().includes(value)
      )
    );
    renderBlocks(filtered);
  });
};

// Auto-refresh every 10 seconds
setInterval(() => {
  loadBlockchain();
}, 10000);

// Dark mode toggle
function toggleTheme() {
  document.body.classList.toggle("dark-mode");
}

// Navigate to block detail page
function viewBlock(index) {
  window.location.href = `/explorer/block.html?id=${index}`;
}