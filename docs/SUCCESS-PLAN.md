# 30-Day Preview Evaluation Plan

This guide helps you evaluate the Reveal AI Add-On and provide meaningful feedback to shape the final product.

> **Remember**: This is a **private preview for evaluation only**. The goal is to test the capabilities, find issues, and help us understand what works and what doesn't. This is **not** a production deployment.

---

## Overview

**Duration**: 30 days
**Your Role**: Test the AI capabilities and provide honest feedback
**Our Goal**: Use your input to improve the product before RTM

---

## Week 1: Installation & First Impressions (Days 1-7)

### Goals
- Get the AI Add-On running in your environment
- Generate your first AI-powered dashboard
- Understand basic capabilities

### Activities

**Day 1-2: Setup** (2-3 hours)
- Follow the [Getting Started Guide](GETTING-STARTED.md)
- Install NuGet package from local feed
- Configure your LLM provider (OpenAI or Anthropic)
- Generate metadata for one datasource

**Day 3-7: Explore & Test** (4-6 hours)
- Try 10-20 different natural language prompts
- Vary complexity: simple to complex
- Test with your actual business data
- Note what works well and what doesn't

### Feedback to Capture

**Installation Experience:**
- How long did setup actually take?
- What was confusing or unclear?
- Any blockers or issues?

**First Impressions:**
- How accurate are the generated dashboards?
- What surprised you (positively or negatively)?
- What's missing that you expected?

### Check-in
**End of Week 1** - 30-minute call with Reveal team
- Demo what you've built
- Discuss initial feedback
- Answer questions

---

## Week 2-3: Deeper Exploration (Days 8-21)

### Goals
- Test with more datasources and scenarios
- Explore advanced features
- Find edge cases and limitations

### Activities

**Expand Testing** (6-8 hours)
- Add 2-3 more datasources
- Test complex scenarios:
  - Multi-table queries
  - Filters and calculations
  - Different visualization types
- Try the chat interface (if applicable)
- Test widget analysis (if applicable)

**Document Issues** (ongoing)
- Keep notes on bugs you encounter
- Screenshot unexpected results
- Track prompts that don't work as expected

**Security & Performance** (2-3 hours)
- Review what data is sent to the LLM
- Check performance with your data volume
- Note any security concerns

### Feedback to Capture

**What Works:**
- Which features are most valuable?
- What exceeds expectations?

**What Doesn't:**
- Where does the AI fail or produce poor results?
- What's frustrating or confusing?
- Performance issues?

**What's Missing:**
- Features you expected but didn't find
- Capabilities that would make this more useful

**API Design Feedback (Critical):**
- **ASP.NET Web API Routes**: Do the routes make sense? Would you structure them differently?
- **Client API (@revealbi/api)**: Is the TypeScript/JavaScript API intuitive? What would you change?
- **Request/Response formats**: Are they clear? Missing properties? Too complex?
- **Scenarios not well-supported**: What are you trying to do that the current API doesn't handle well?

### Check-in
**End of Week 2 & 3** - Quick 15-20 minute sync
- Progress update
- Discuss any blockers
- Clarify questions

---

## Week 4: Final Feedback (Days 22-30)

### Goals
- Wrap up testing
- Submit comprehensive feedback
- Discuss RTM requirements

### Activities

**Final Testing** (2-3 hours)
- Test any remaining scenarios
- Verify previous issues
- Try latest updates (if any)

**Feedback Compilation** (2 hours)
- Complete feedback form (below)
- Prioritize your top requests
- Document critical blockers

### Check-in
**End of Week 4** - 30-minute wrap-up call
- Share final feedback
- Discuss what would make this production-ready for you
- Review roadmap and timeline

---

## Quick Feedback Channels

Don't wait for the formal review - share feedback anytime:

- **Discord**: #reveal-sdk-ai-preview (fastest)
- **GitHub Issues**: https://github.com/RevealBi/sdk-samples-ai/issues

---

## What We're Looking For

**Be Honest:**
- We want to hear what doesn't work
- Critical feedback is valuable
- Don't hold back on issues

**Be Specific:**
- "Dashboard generation is slow" → "Generating dashboards with 50+ fields takes 45+ seconds"
- "AI is inaccurate" → "When I ask for 'sales by region', it creates a pie chart instead of a bar chart"

**Share Use Cases:**
- What problems are you trying to solve?
- How would you use this in your product?
- What would make this a "must-have"?

---

## Success Criteria (For Us)

We consider the preview successful if:

- You can successfully generate dashboards for common scenarios
- You understand the value proposition
- We receive detailed feedback on what to improve
- You can articulate what would make this production-ready
- We identify and fix critical blockers

**This is about learning, not shipping.** Your honest feedback directly shapes the final product.

---
