# MyCoffee Design Guidelines

These guidelines define the product language for MyCoffee. New screens and features should extend this system rather than introduce a new visual style. The goal is a calm, practical companion for people who care about coffee: clear like a productivity app, with the warmth and personality of a specialty coffee shop.

## Product Principles

- Make the user's next question obvious. Stock answers "what do I have?" and Brew answers "what did I do?".
- Prioritize usability, information hierarchy, consistency, personality, then decoration.
- Prefer calm, fast interactions. Animation is functional only: transitions, feedback, and expand/collapse.
- Preserve the user's data and existing workflows when changing presentation.
- Do not invent data. In particular, never imply remaining coffee quantity when only the recorded bag weight is known.

## Visual Direction

MyCoffee is dark-first, cozy, and professional. It should feel roasted and tactile without turning every surface brown.

- Dark backgrounds use near-black espresso/charcoal, not pure black.
- Text is warm off-white, not pure white by default.
- Copper/terracotta is the primary accent for actions and active states.
- Cream and parchment tones support light mode and secondary emphasis.
- Sage is a restrained natural accent for low-priority status indicators.
- Use color to communicate hierarchy and state, not as decoration.
- Maintain accessible contrast for text, controls, and status indicators in both themes.

Use the shared `MaterialTheme.colorScheme` roles. Do not add screen-specific colors unless a new semantic role is genuinely needed. The source of truth is `ui/theme/Color.kt` and `ui/theme/Theme.kt`.

## Typography

- Use the theme typography scale; do not create one-off text sizes for individual screens.
- Screen titles may use the theme's editorial serif treatment.
- Use sans-serif body text for clarity and scanning.
- Titles identify the screen or primary object. Section labels organize content. Metadata is visibly secondary.
- Avoid making every field bold or large.
- Use uppercase labels sparingly for quiet section markers such as `RECENT BREWS` or `CURRENTLY OPEN`.

## Layout And Spacing

- The app shell owns the top bar, bottom navigation, and their insets. Destination screens must not add a second scaffold for shell UI.
- Use comfortable, consistent spacing. Prefer existing spacing values and avoid arbitrary per-screen rhythm.
- Content should be information-dense enough to scan, but never cramped.
- Add bottom content clearance when a floating action button overlays a scrolling list.
- Keep primary content comfortably inside the screen edges; use 16dp as the normal compact content inset.
- Design for compact phones first while keeping layouts structurally ready for wider windows.

## Surfaces And Corners

- A card or bordered surface must group related information or provide a clear interaction boundary.
- Prefer a mix of flat background, subtle surface contrast, borders, and dividers.
- Use restrained corner radii, typically 8-12dp. Avoid making every element a pill.
- Avoid large shadows, glassmorphism, gradients, and decorative containers.
- Use filled surfaces for meaningful grouping, not for every text block.

## Navigation And Actions

- Keep the three primary destinations simple: Stock, Brew, and Settings.
- The selected destination must be unambiguous through icon, label, and restrained accent treatment.
- The primary create action may use a FAB where it is discoverable and does not cover list actions.
- Keep edit and delete actions visually subordinate to the coffee or brew itself.
- Prefer overflow/contextual actions or small icon buttons over large action clusters beside titles.
- Destructive actions must be clearly labeled, confirmed where appropriate, and visually restrained.
- Do not place important actions underneath a FAB, system bar, or clipped content.

## Stock UI

The Stock screen should answer how much coffee is available and what is currently open at a glance.

- Lead with a compact shelf summary: total active, open, and unopened bags.
- Give open coffee the strongest visual emphasis and a clear status indicator.
- Present unopened bags as a scannable list; finished bags belong in a secondary expandable section.
- Preserve coffee name, roaster, origin, species, process, weight, dates, tasting notes, and state.
- Show metadata as compact supporting information. Do not render every field as an equally prominent `Label: value` block.
- Use status dots or restrained semantic color in addition to state text. Never rely on color alone.
- Weight is the recorded bag/current weight. Do not show a fake remaining-quantity progress indicator.
- Ensure finished-bag edit/delete actions remain reachable when the FAB is present.

## Brew UI

The Brew screen is a journal, not a database dump.

- Lead each entry with coffee name, brew method, and date.
- Show a compact recipe snapshot: dose, yield, and brew time are usually the most useful list details.
- Keep ratings tasteful and informative; avoid gamified visual treatment.
- Group detailed information into Coffee, Recipe, Brewing, and Result when a detail view is added.
- Make old recipes easy to reconstruct without giving every field equal emphasis.
- Analytics are supporting context and should remain compact. They must not dominate the journal.

## Settings UI

Settings are understated and conventional.

- Group preferences with clear section headings and dividers.
- Use radio controls for a short mutually exclusive list; use a dropdown for a longer method list.
- Keep Save quiet and only show it when it represents a real action. Do not use a large filled button for a secondary settings action.
- Make every control reachable without relying on accidental scroll position.
- Do not duplicate the app shell or introduce destination-specific top/bottom bars.

## Forms And Dialogs

- Group fields by user intent, such as identity, origin/profile, recipe, and notes.
- Use date pickers for dates, dropdowns for enumerations, and numeric-friendly input for measurements.
- Use sensible defaults without overwriting existing data during edits.
- Keep the primary action visible and reachable. Long content may scroll, but actions must not disappear below an unconstrained form.
- Show validation close to the invalid field and provide clear supporting text.
- Preserve the existing callback and data model when redesigning a form.

## Empty, Loading, And Error States

- Empty states should explain what the screen is for and provide the next appropriate action.
- Keep empty states restrained: one short explanation and one clear action is usually enough.
- Loading, error, and partial-data states must remain understandable and recoverable.
- Never silently replace missing coffee data with misleading information.

## Accessibility And Interaction

- Every icon-only action needs a meaningful content description.
- Keep touch targets at least platform-recommended sizes, even when the visual icon is small.
- Never communicate state through color alone; pair color with text, shape, or an icon.
- Preserve logical reading and focus order when layouts change.
- Use labels that describe the action, not implementation details.

## Compose Implementation Rules

- Put shared visual primitives in `composeApp/src/commonMain` when they work across targets.
- Reuse `MaterialTheme`, shared components, and semantic color roles before creating new primitives.
- Keep screens responsible for state and orchestration according to the existing project architecture; keep reusable components stateless where practical.
- Use stable keys for lazy lists and avoid expensive formatting or filtering in item composition.
- Do not add animation unless it improves feedback or orientation.
- Add or update Compose tests for visible labels and important interactions when changing a component.

## Review Checklist

Before merging a UI feature, verify:

- Does the screen have one clear primary purpose and hierarchy?
- Does it use the shared coffee palette and typography rather than custom styling?
- Are surfaces purposeful rather than containers by default?
- Are primary, secondary, and destructive actions visually distinct?
- Can all content and actions be reached on a compact screen?
- Does the feature work in both light and dark themes?
- Are empty, error, and accessibility states handled?
- Were existing data, callbacks, navigation, and tests preserved?
