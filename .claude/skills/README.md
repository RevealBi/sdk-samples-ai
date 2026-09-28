# Agent Skills for Ignite UI for Web Components

Task-focused instructions that help AI coding agents use **Ignite UI for Web Components** (`igniteui-webcomponents`) correctly in the sample clients: choosing components, wiring them into plain HTML, theming, and keeping the download small.

These are the official skills shipped in the `igniteui-webcomponents` npm package (`node_modules/igniteui-webcomponents/skills/`), copied unchanged from **version 7.4.1**, the version the clients load. When you upgrade the library, copy the matching skills from the new package.

## Available skills

| Skill | Use when |
| --- | --- |
| [igniteui-wc-choose-components](./igniteui-wc-choose-components/SKILL.md) | Deciding which `igc-*` component fits a UI pattern (chat, navigation, forms, feedback) |
| [igniteui-wc-integrate-with-framework](./igniteui-wc-integrate-with-framework/SKILL.md) | Registering components and loading a theme. The clients here are plain HTML, see `references/vanilla-js.md` |
| [igniteui-wc-customize-component-theme](./igniteui-wc-customize-component-theme/SKILL.md) | Applying the brand palette, sizing, or restyling a component through `::part()` and CSS variables |
| [igniteui-wc-generate-from-image-design](./igniteui-wc-generate-from-image-design/SKILL.md) | Building a view from a screenshot or mockup |
| [igniteui-wc-optimize-bundle-size](./igniteui-wc-optimize-bundle-size/SKILL.md) | Registering only the components a page uses |

Agents pick a skill from the request, so ordinary questions work: *"add a settings dialog"*, *"match this screenshot"*, *"make the buttons violet"*. You can also name one: *"use the igniteui-wc-customize-component-theme skill"*.

## MCP servers

[`.mcp.json`](../../.mcp.json) at the repo root configures two MCP servers that make the agent's answers authoritative rather than recalled:

- **`igniteui-cli`**: component docs and API reference (`list_components`, `get_doc`, `get_api_reference`, `search_docs`, `search_api`)
- **`igniteui-theming`**: palette, typography, elevation and per-component theme generation

Both run through `npx`, so Node.js must be installed. Claude Code asks you to approve project MCP servers the first time. Every skill still works without them.

## Other agents

Claude Code loads skills from `.claude/skills/`. GitHub Copilot in VS Code also reads this folder. For other agents, point them at this folder or copy it into the directory they read. Keep each `SKILL.md` next to its `reference/` or `references/` folder, because the skills link to each other with relative paths.
