# Petanque Counter — bottom-up TODO

Rebuild as a fresh v2. Unity phone UI, Zepp OS watch scoring, and Temp screenshots are **guidelines only**. No migration of old stored values.

**IDs** (app + wear share these)

- `applicationId`: `nl.siwoc.PetanqueCounter`
- `namespace`: `nl.siwoc.petanquecounter`
- Kotlin: `:app` → `nl.siwoc.petanquecounter.app.MainActivity`; `:wear` → `nl.siwoc.petanquecounter.wear.MainActivity`
- `:core` library namespace: `nl.siwoc.petanquecounter.core`

**Baked-in product choices** (object if wrong)

- Phone and Wear: ± opens a 1–6 mène overlay; Undo; Reset.
- Phone also: 3 single-hand layouts (Unity).
- Wear layout: Zepp OS + Temp wear shots.
- Each device stores its own DataStore. No cross-device sync or companion.

---

## Layer 0 — Gradle / modules

- [ ] Add Kotlin to `:core` (library is Java-only today).
- [ ] Catalog: DataStore, Hilt, Coroutines, Lifecycle ViewModel, Wear Compose already present.
- [ ] `:app` and `:wear` `implementation(project(":core"))`.
- [ ] Hilt plugins on root / app / wear / core.
- [ ] Align Wear `versionName` with phone (`2.0.0`); give Wear a distinct `versionCode` if both ship on the same Play listing.

---

## Layer 1 — Domain (`:core`, no UI)

Package: `nl.siwoc.petanquecounter.core.domain`

- [ ] `Team` (`Nous`, `Eux`).
- [ ] `PhoneLayout`: `ButtonsRight`, `ButtonsLeft`, `ButtonsCenter` (Temp `app-main0/1/2`).
- [ ] `GameState(nousScore, euxScore, layout, history)` — history is snapshots for Undo.
- [ ] Rules (pure functions, unit-tested):
  - scores never go below 0
  - mène delta is ±1..±6
  - minus mène cannot exceed current score
  - `WIN_SCORE = 13` → `isWinReached` (gold score; play continues)
  - `cycleLayout()` walks the three phone layouts
  - `applyMene` pushes a snapshot first (Zepp OS `pushHistory`)
  - `undo` pops last snapshot
  - `reset` zeros scores and clears history

---

## Layer 2 — Persistence (`:core`)

Package: `nl.siwoc.petanquecounter.core.data`

- [ ] DataStore Preferences: `nousScore`, `euxScore`, `layout`, `history`.
- [ ] Fresh defaults only (`0`, `0`, `ButtonsCenter`, empty history).
- [ ] `ScoreRepository`: `state: Flow<GameState>` + suspend writes.
- [ ] History as a compact string/list in Preferences (enough for undo; not a full match log).

---

## Layer 3 — Controller (`:core`)

Package: `nl.siwoc.petanquecounter.core.controller`

- [ ] `ScoreController` on top of the repository (UDF, no Compose):
  - `state: StateFlow<GameState>`
  - `applyMene(team, delta)` — phone and wear
  - `undo()` / `reset()` / `cycleLayout()`
- [ ] Expose derived flags: minus enabled, undo available, win reached.

---

## Layer 4 — DI + tests (`:core`)

- [ ] Hilt: `@Singleton` DataStore, repository, controller.
- [ ] Unit tests for domain rules + controller (mène clamp, undo stack, reset, layout cycle).

---

## App implementation (`:app`)

Package: `nl.siwoc.petanquecounter.app`  
Entry: `MainActivity`  
Guideline: Temp `app-main0.png`, `app-main1.png`, `app-main2.png`, `app-reset.png`

- [ ] `@HiltAndroidApp` + Hilt `MainActivity`.
- [ ] `ScoreViewModel` wraps `ScoreController` (logic stays out of composables).
- [ ] `AppTheme` / M3 colors toward Unity: blue score panels, red/white frame, dark control well, red Reset.
- [ ] Main screen:
  - Nous | Eux scores (gold at ≥ 13)
  - ± buttons placed by `PhoneLayout` — open the mène overlay (not ±1)
  - **Undo** last mène
  - **Reset** → bilingual confirm (FR + EN, Unity overlay)
  - **Layout** → `cycleLayout()`
  - mascot → About / privacy (`Temp/PrivacyPolicy.html` as copy source)
- [ ] **Mène overlay** (same rules as wear: 1–6, green add / red subtract, clamp below 0)
- [ ] Strings `values` + `values-fr` (app name already: Petanque Counter / Compteur de Pétanque).
- [ ] Use `Temp/icon.png` for launcher if it is the intended icon.
- [ ] Compose previews for the three layouts + mène overlay.

---

## Wear implementation (`:wear`)

Package: `nl.siwoc.petanquecounter.wear`  
Entry: `MainActivity`  
Guideline: Temp `wear-square-main.png`, `wear-square-mene.png`, `wear-round-main.jfif` + Zepp OS `page/index` + `page/mene`

- [ ] Manifest `android:name=".wear.MainActivity"`; keep standalone.
- [ ] Theme under `nl.siwoc.petanquecounter.wear.ui.theme`.
- [ ] `ScoreViewModel` wraps the same `ScoreController`.
- [ ] **Main score screen**
  - two columns: Nous / Eux, large scores
  - ± pair per team (blue)
  - Undo (gray) / Reset (red)
  - minus disabled at 0
  - score color gold at ≥ 13
  - round: circular targets (Temp round shot); square: rounded rects (Temp square / Zepp OS)
- [ ] **Mène overlay** (`wear-square-mene.png`)
  - title `{Nous|Eux} · {+|−}`
  - 2×3 grid 1–6; green add / red subtract
  - disable digits that would go below 0
  - **Retour** / Back cancels
- [ ] Reset: confirm dialog (visible Reset button on Wear shots).
- [ ] Undo: toast when history is empty (Zepp OS `undo_empty`).
- [ ] i18n EN + FR (`Nous`, `Eux`, Undo, Reset, Retour, confirm copy).
- [ ] Wear Compose previews for round and square.

---

## Order of work

1. Layer 0 (wire modules)  
2. Layers 1–4 (core, tests)  
3. App main + layouts + mène overlay + undo + reset  
4. Wear main + mène overlay  
5. Icons, privacy/about, string polish
