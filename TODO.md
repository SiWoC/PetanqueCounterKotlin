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
- Action icons are Material Symbols XML in `:core` (no icon library): Undo `ic_history`, Reset `ic_refresh`, Layout `ic_dashboard_customize`. Phone About: `schnappi`.
- Wear layout: Zepp OS + Temp wear shots.
- Each device stores its own DataStore. No cross-device sync or companion.

---

## Layer 0 — Gradle / modules

- [x] Add Kotlin to `:core` (library is Java-only today).
- [x] Catalog: DataStore, Coroutines, Lifecycle ViewModel (Wear Compose already present).
- [x] `:app` and `:wear` `implementation(project(":core"))`.
- [x] Shared versioning (`PetanqueVersion`): `versionName` 2.0.0; `versionCode` = targetSdk × 10_000_000 + type × 1_000_000 + major × 10_000 + minor × 100 + patch (phone type 0 → 360020000, wear type 5 → 365020000).

---

## Layer 1 — Domain (`:core`, no UI)

Package: `nl.siwoc.petanquecounter.core.domain`

- [x] `Team` (`Nous`, `Eux`).
- [x] `PhoneLayout`: `ButtonsRight`, `ButtonsLeft`, `ButtonsCenter` (Temp `app-main0/1/2`).
- [x] `GameState(nousScore, euxScore, layout, history)` — history is snapshots for Undo.
- [x] Rules (pure functions, unit-tested):
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

- [x] DataStore Preferences: `nousScore`, `euxScore`, `layout`, `history`.
- [x] Fresh defaults only (`0`, `0`, `ButtonsCenter`, empty history).
- [x] `ScoreRepository`: `state: Flow<GameState>` + suspend writes.
- [x] History as a compact string/list in Preferences (enough for undo; not a full match log).

---

## Layer 3 — Controller (`:core`)

Package: `nl.siwoc.petanquecounter.core.controller`

- [x] `ScoreController` on top of the repository (UDF, no Compose):
  - `state: StateFlow<GameState>`
  - `applyMene(team, delta)` — phone and wear
  - `undo()` / `reset()` / `cycleLayout()`
- [x] Expose derived flags: minus enabled, undo available, win reached.

---

## Tests (`:core`)

- [x] Unit tests for domain rules + controller (mène clamp, undo stack, reset, layout cycle).

---

## App implementation (`:app`)

Package: `nl.siwoc.petanquecounter.app`  
Entry: `MainActivity`  
Guideline: Temp `app-main0.png`, `app-main1.png`, `app-main2.png`, `app-reset.png`

- [x] `ScoreViewModel` constructs `ScoreController` (logic stays out of composables).
- [x] `AppTheme` / M3 red–white–blue (flag palette); gold scores at ≥ 13; red Reset.
- [x] Main screen:
  - Nous | Eux scores (gold at ≥ 13)
  - ± buttons placed by `PhoneLayout` — open the mène overlay (not ±1)
  - **Undo** last mène (`ic_history`)
  - **Reset** → bilingual confirm (FR + EN) (`ic_refresh`)
  - **Layout** → `cycleLayout()` (`ic_dashboard_customize`)
- [ ] mascot (`schnappi`) → About / privacy (`Temp/PrivacyPolicy.html` as copy source)
- [x] **Mène overlay** (same rules as wear: 1–6, green add / red subtract, clamp below 0)
- [x] Strings `values` + `values-fr` (app name already: Petanque Counter / Compteur de Pétanque).
- [ ] Use `Temp/icon.png` for launcher if it is the intended icon.
- [x] Compose previews for the three layouts + mène overlay.

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
  - Undo (gray, `ic_history`) / Reset (red, `ic_refresh`)
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
2. Layers 1–3 (core) + tests  
3. App main + layouts + mène overlay + undo + reset  
4. Wear main + mène overlay  
5. Wear polish
