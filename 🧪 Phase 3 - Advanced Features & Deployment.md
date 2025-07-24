## 🧪 Phase 3: Advanced Features & Deployment

| #  | Task                                            | Description                                                                                 | Suggested Branch Name                  | Is Done        |
|----|-------------------------------------------------|---------------------------------------------------------------------------------------------|----------------------------------------|----------------|
| 1  | 🚀 **Block Propagation via REST API**           | Simulate inter-node syncing via REST endpoints (`/receive-block`, `/peer-status`, etc.)    | `feature/rest-peer-sync`               |⬜              |
| 2  | 🔒 **Consensus Algorithm Abstraction**          | Allow swapping PoW with PoS or other algorithms via a strategy interface                    | `feature/consensus-interface`          |⬜              |
| 3  | 🧪 **Smart Contract Mini VM (Optional)**         | Implement a minimal VM to execute simple smart contracts                                    | `feature/smart-contracts`              |⬜              |
| 4  | 📊 **Dashboard with Charts**                    | Show transaction volume, top users, recent blocks as graphs (Chart.js, Recharts, etc.)     | `feature/dashboard-analytics`          |⬜              |
| 5  | 🧱 **UTXO-Based Transaction Model (Optional)**  | Switch from account-based to UTXO-style blockchain                                          | `feature/utxo-support`                 |⬜              |
| 6  | 🧑‍⚖️ **Permissioned Chain Roles**              | Add user roles like miner, validator, client (restrict who can mine or view full chain)    | `feature/permission-roles`             |⬜              |
| 7  | 🔐 **JWT-based Auth for REST APIs**             | Protect endpoints with login-based auth and JWT tokens                                     | `feature/jwt-auth-api`                 |⬜              |
| 8  | 🌐 **Deploy to Render/Heroku/Fly.io**           | Make your blockchain publicly accessible (or deploy only the explorer for demo)             | `deploy/blockchain-demo`               |⬜              |

> **Use these Unicode symbols accordingly based on the task status: ✅ (U+2705) for completed, ⬜ (U+2B1C) for not started, and ☑️ (U+2611) for in progress or partially done.**

### 🧩 Notes & Enhancements

- Abstract the consensus logic early to enable plug-and-play upgrades later.
- Consider using **JWT**, **Spring Security**, and **role-based access control** for task 7.
- Integrate `Chart.js` or `Recharts` into the existing explorer for task 4.
- Deploy only the explorer first (static files) for a public demo via Render/Netlify.
- Use **JUnit**, **MockMvc**, and **TestRestTemplate** to test new REST endpoints.

> **Choose one task to begin Phase 3** — and I’ll guide you with branch name, structure, and detailed implementation steps.
