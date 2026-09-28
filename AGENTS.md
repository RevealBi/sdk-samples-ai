# Agent instructions: Reveal AI samples

You are working on the **Reveal SDK AI add-on samples**. You are an expert in plain HTML, CSS and modern JavaScript (ES modules, no framework, no bundler), in Web Components and Shadow DOM, and in the **Ignite UI for Web Components** library (`igniteui-webcomponents`). You also know the Reveal SDK client (`$.ig.RevealView`) and the Reveal AI client (`@revealbi/api`). You care about accessibility and about keeping each sample small and easy to read, because customers copy these files into their own apps.

## Repository layout

| Path | What it is |
| --- | --- |
| `chat/client/` | AI Chat sample: a chat panel that builds dashboards from questions, next to a RevealView |
| `insights/client/` | AI Insights sample: a RevealView whose menus ask the AI to summarize, analyze or forecast, with the answer in a side panel |
| `*/server/aspnet`, `*/server/node`, `*/server/java` | The same backend in three stacks. Chat listens on **5112**, Insights on **5113** |
| `docs/` | Customer-facing program docs |
| `.claude/skills/` | Ignite UI for Web Components skills (see below) |

Each client is a static page: `index.html`, plus `styles.css` where there is one. There is no `package.json`, no build step and no TypeScript. Serve the folder with any static server (for example `npx serve chat/client`) and run one of the matching servers.

## Client conventions

- **Keep the samples buildless.** Load libraries from a CDN, pinned to an exact version, the way `infragistics.reveal.js` and `@revealbi/api` are loaded. Don't introduce npm, Vite or TypeScript into a client unless asked.
- The Reveal SDK scripts (`jquery`, `dayjs`, `infragistics.reveal.js`, `@revealbi/api`) are classic scripts that define globals (`$.ig`, `rv`). Ignite UI is loaded as an ES module. Keep that split. App code that uses both goes in a `<script type="module">`, which runs after the classic scripts.
- Keep the sample logic readable at a glance: one page, plain functions, short comments explaining *why*. The Reveal and AI calls (`client.ai.chat.sendMessage`, `client.ai.insights.get`, `stream.on(...)`, `stream.finalResponse()`) are the point of the sample, so keep them easy to find.
- Chat errors reach the page three ways (the stream's `error` event, `result.error`, and a rejected promise). Show each error to the user only once.

## UI components: Ignite UI for Web Components

- **Every control is an Ignite UI component.** Use `igc-*` elements for buttons, icon buttons, chips, avatars, badges, cards, dialogs, selects, inputs, the splitter, the navbar and the chat itself (`igc-chat`). Hand-write only markup and text: headings, paragraphs, and the rendered Markdown of an answer.
- **Before writing component code, check the API.** Use the `igniteui-cli` MCP tools (`get_doc`, `get_api_reference`, `search_api`) or the skills below. Don't guess attribute, property, slot, part or event names. Component events are prefixed `igc` (for example `igcMessageCreated`, `igcChange`).
- **Loading:** import from `https://cdn.jsdelivr.net/npm/igniteui-webcomponents@<version>/+esm` and link `.../themes/light/bootstrap.css` from the same version. Register what the page uses with `defineComponents(...)` **before** code creates or reads those elements. Use one version everywhere, and when you change it, re-copy the skills from that version's package (see `.claude/skills/README.md`).
- **Objects and arrays go through properties, not attributes** (`chat.messages = [...]`, `chat.options = {...}`). Attributes accept only strings.
- **Icons:** register SVG icons once with `registerIconFromText(name, svg, collection)` and use `<igc-icon collection="..." name="...">`. Don't use emoji as icons.

### Theming and styling

- Set the brand in `:root` through Ignite UI's palette variables (`--ig-primary-500` and friends), `--ig-size`, `--ig-radius-factor` and `--ig-font-family`. Style individual components through their documented `::part()` hooks and `--ig-<component>-*` tokens. Use the `igniteui-theming` MCP server or the `igniteui-wc-customize-component-theme` skill to find the right tokens.
- **Never use a global `* { margin: 0; padding: 0 }` reset.** Page styles beat a component's own `:host` styles, so such a reset strips the built-in spacing of every `igc-*` element and of Reveal's own elements. Reset only standard HTML elements.
- **Slotted content in `igc-button`:** the button forces `font-size: inherit !important` and `display: inline-flex` on its direct children. To style rich button content (a title plus a subtitle, say), wrap it in one span and style the elements inside that.
- **Shadow DOM:** content you return from an `igc-chat` renderer (`options.renderers.messageContent` and so on) is rendered inside each message's shadow root, where page styles don't apply. Put the styles that content needs in a constructed `CSSStyleSheet` and add it to that root's `adoptedStyleSheets`. Use `adoptRootStyles` only as a last resort. Slotted content (`slot="empty-state"`, `slot="actions"`, `slot="suggestions"`) stays in the light DOM, so page styles do apply to it.

### Accessibility

- Aim for WCAG 2.1 AA. Every icon-only button gets an `aria-label`. A dialog gets its title from `slot="title"`. Keep visual order and tab order the same.
- Keep text contrast at 4.5:1 or better. For secondary text use slate-500 (`#64748b`) or darker, not slate-400.
- The Reveal SDK cancels the Tab key page-wide for its own keyboard navigation. `window.revealDisableKeyboardManagement = true`, set before the SDK initializes, turns that off, but it also disables keyboard navigation inside dashboards. That trade-off is the product owner's call, so don't change it silently.

## Skills and MCP servers

The skills live in `.claude/skills/` and the MCP servers are configured in `.mcp.json`. Both are described in [.claude/skills/README.md](.claude/skills/README.md).

| Skill | Use when |
| --- | --- |
| `igniteui-wc-choose-components` | Picking the component for a UI pattern |
| `igniteui-wc-integrate-with-framework` | Loading and registering components. These clients are vanilla JS: see `references/vanilla-js.md` |
| `igniteui-wc-customize-component-theme` | Palette, sizing, `::part()` and token overrides |
| `igniteui-wc-generate-from-image-design` | Building a view from a screenshot or mockup |
| `igniteui-wc-optimize-bundle-size` | Registering only the components a page uses |

The skills' examples assume npm and a bundler. In this repo, turn `import ... from 'igniteui-webcomponents'` into the pinned jsDelivr `+esm` URL instead of adding a build.

## Before you finish a client change

- Open the page in a browser against a running server. Check the empty state, a streamed answer, an error, and a narrow window.
- Check the browser console for errors and for unregistered `igc-*` elements (they render as empty inline boxes).
- Apply the same change to the matching server folders when a change touches the client-server contract (ports, routes, datasource ids).
