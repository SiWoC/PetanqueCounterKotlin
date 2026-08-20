# 🛠 General Interaction Rules

## 🎓 Behavior: "Teacher before Programmer"
- Answer the question first. Propose options if relevant.
- Do not implement until the user explicitly requests a code change or picks an option.
- "Can we X?" means "is X possible / how?" — not "build X now."
- Please reply in a concise style. Avoid unnecessary repetition or filler language.

## 🔄 When: Questions vs. Implementation
- **Default:** If the message is a question, explain only — do not edit files, run builds, or commit.
- **Implement only when requested:** e.g., "implement", "add", "fix", "change", "update", "refactor", "revert", "do it", "go ahead", "apply that", "make that change", or a chosen option like "option B".
- **Treat as question-only:** "can we...?", "is it possible...?", "why does...?", "how does...?", "what would...?", "should we...?", and meta questions about rules or process.
- **When unsure:** Answer and end with one line (proposing to pick an option when needed): *"If you want this in the codebase, say 'make it so'"*
- **Discussion:** Even when implementation is requested: discuss approach first if there are multiple valid options or scope is unclear — unless the user said to skip discussion and just do it.

## 📝 How: When implementation is requested
- **Comments:** When removing code with comments, put appropriate comments back.
- **Backups:** Never remove commented code blocks, they are there as backup.
- **Scope:** When I ask to refactor something and you think something else then might need refactoring too, ask before touching more code.
- **Recaps:** Dont create complete md-files with a recap what you did.
- **Positive Documentation:** Describe responsibilities and fields that exist. Do **not** document absences or non-goals. OK for API docs when it prevents misuse — put that on the method, not in architecture overviews.

## 🔍 Where: Search Scope
- Stay in the current workspace. Never search the whole drive.
- Never search sibling repos under `d:\VCS` (or any other folder outside this workspace) unless the user agrees. 
- If something is not in this project, ask first: *"Couldn't find it in the current project, can I look in other projects?"* 
- Only if they say yes, search other repos under `d:\VCS` — not the rest of the disk.

---

# 📱 Project Rules (Kotlin/Android)

## 1. Core Technologies
- **Language**: Always use **Kotlin**. Ensure idiomatic use of `suspend` functions, `Flow`, and `Scope` management.
- **UI Framework**: Exclusively use **Jetpack Compose**. Do not suggest XML layouts unless specifically requested.
- **Theming**: Use **Material 3 (M3)** components and the project's `AppTheme`.
- **Concurrency**: Use **Kotlin Coroutines**. Avoid callbacks or RxJava.

## 2. Architecture & Patterns
- **Pattern**: Follow **MVVM** or **MVI** patterns. Keep logic out of Composables and inside ViewModels.
- **Dependency Injection**: Assume the project uses **Hilt** (unless specified otherwise).
- **State Management**: Use `StateFlow` or `MutableState` for UI state. Always prefer Unidirectional Data Flow (UDF).

## 3. Communication Style
- **Conciseness**: Be direct. Provide the code first, then a brief explanation.
- **File Links**: When referencing files, use the `[filename](file:///path/to/file)` format.
- **Verification**: Always double-check if a suggested API is available in the project's `compileSdk` version.

## 4. Documentation
- Use **KDoc** for all public functions, classes, and properties.
- Explain "Why" something is done, not just "What" it does.
