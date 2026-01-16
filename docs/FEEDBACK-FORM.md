# Reveal AI Add-On - Preview Feedback Form

Thank you for participating in the Reveal AI Add-On private preview! Your feedback directly shapes the final product.

---

## 1. Overall Experience

**How would you rate the AI Add-On? (1-10):** ___

**Would you use this in production if available today?**
- [ ] Yes, absolutely
- [ ] Yes, but only after [specific issues] are fixed
- [ ] Maybe, needs significant improvements
- [ ] No, not suitable for our use case

**Why?** ___

---

## 2. What Works Well

List 3-5 things you like or that work well:

1. ___
2. ___
3. ___
4. ___
5. ___

---

## 3. Top Issues & Bugs

List bugs or issues you encountered (most critical first):

| Issue Description | Severity | How to Reproduce |
|-------------------|----------|------------------|
| | Critical / High / Medium / Low | |
| | Critical / High / Medium / Low | |
| | Critical / High / Medium / Low | |
| | Critical / High / Medium / Low | |
| | Critical / High / Medium / Low | |

---

## 4. Missing Features

What capabilities are missing or incomplete?

**Must-Have** (blocks production use):
- ___
- ___
- ___

**Should-Have** (significant value):
- ___
- ___
- ___

**Nice-to-Have** (future enhancement):
- ___
- ___
- ___

---

## 5. Dashboard Accuracy

**How accurate are generated dashboards? (1-10):** ___

**What % of prompts produce usable results?** ___ %

**Examples of prompts that worked well:**
- ___
- ___
- ___

**Examples of prompts that failed or produced poor results:**
- ___
- ___
- ___

---

## 6. Performance

**Average dashboard generation time:** ___ seconds

**Is this acceptable?**
- [ ] Yes
- [ ] No - I need it under ___ seconds

**Metadata generation time:** ___ minutes

**Any performance concerns or bottlenecks?** ___

---

## 7. API Design & Usability

### ASP.NET Web API (Server-Side)

**Route design rating (1-10, 10=excellent):** ___

**Current routes:**
- `POST /api/reveal/ai/dashboards` - Dashboard generation
- `POST /api/reveal/ai/chat` - Chat messages
- `POST /api/reveal/ai/insights` - Widget insights
- `GET /api/reveal/ai/datasources` - List datasources
- `GET /api/reveal/ai/providers` - List LLM providers
- `GET /api/reveal/ai/metadata/status` - Metadata status

**What would you change about the route structure?** ___

**Are the routes intuitive and discoverable?** ___

**Request/Response format feedback:**
- What's unclear or confusing? ___
- Missing properties in responses? ___
- Unnecessary complexity? ___

**Suggested API improvements:**
1. ___
2. ___
3. ___

### Client-Side API (JavaScript/TypeScript - @revealbi/api)

**Client API ease of use (1-10, 10=excellent):** ___

**Which pattern do you prefer?**
- [ ] Singleton pattern (`RevealSdkClient.getInstance()`)
- [ ] Direct instantiation (`new RevealSdkClient()`)
- [ ] Both are fine

**API Method Usage Experience:**

Rate the ease of use for each API method (1-10, 10=excellent):
- `client.ai.dashboards.generate()`: ___
- `client.ai.chat.sendMessage()`: ___
- `client.ai.insights.get()`: ___
- `client.ai.datasources.list()`: ___
- `client.ai.providers.list()`: ___
- `client.ai.metadata.getStatus()`: ___

**What works well about these API methods?**
___

**What's confusing or difficult about these API methods?**
___

**Are the method names intuitive?**
- [ ] Yes
- [ ] No - I would rename: ___

**Are the parameters clear and well-documented?**
- [ ] Yes
- [ ] No - What's unclear: ___

**Do the response objects contain the data you need?**
- [ ] Yes
- [ ] No - Missing data: ___

**Examples of scenarios not well-supported by the current API:**
1. ___
2. ___
3. ___

**Suggested client API improvements:**
1. ___
2. ___
3. ___

---

## 8. Developer Experience

**Setup difficulty (1-10, 10=easy):** ___

**Documentation quality (1-10, 10=excellent):** ___

**What was most confusing during setup?** ___

**What documentation is missing or unclear?** ___

**What would improve the developer experience?**
1. ___
2. ___
3. ___

---

## 9. Use Cases & Value

**What problems are you trying to solve with AI-powered dashboards?**
___

**How would you use this in your product?**
___

**What would make this a "must-have" for your customers?**
___

**What alternative solutions are you considering?**
___

---

## 10. Top 3 Improvements

What 3 changes would make this most valuable to you?

1. ___
2. ___
3. ___

---

## 11. Open Feedback

**Anything else we should know?**
___

**Features you'd like to see in future releases:**
___

**Other comments or suggestions:**
___

---

## Metadata (Optional - Helps Us Understand Context)

**Your Role:**
- [ ] Developer
- [ ] Architect
- [ ] Product Manager
- [ ] Other: ___

**Team Size:** ___

**Industry:** ___

**Use Case Type:**
- [ ] Internal analytics/BI tool
- [ ] Customer-facing embedded analytics
- [ ] SaaS product feature
- [ ] Other: ___

**Number of datasources you tested:** ___

**Primary LLM provider used:**
- [ ] OpenAI
- [ ] Anthropic
- [ ] Google Gemini
- [ ] DeepSeek
- [ ] Other: ___

---

**Thank you for your detailed feedback!** Your input is invaluable in shaping the Reveal AI Add-On.

**Questions?** Contact us in Discord (#reveal-sdk-ai-preview) or via your sales representative.
