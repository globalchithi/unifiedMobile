# Introduction 
The Unified Hub is the go-to application for the mobile team. After we move all Hub workflows to this app,
we will also add in the MobileHub functionality. This one app will be launched from any device (tablet or phone).
The UI will be portrait by default, but should survive rotations and should be scalable on either form factor.

This project is a multi-modular application. Each feature will be organized into its own module so that it
can be dynamically included or excluded in a particular build. This will allow the tablet and phone to have
different functionality from the same application.

---

## Getting Started
### Installation process
* Android Studio Ladybug (or newer) is required to build this application
* Java 21.0.5 (or newer) is required to build this application (packaged with AndroidStudio)
* Gradle 8.10.2 (or newer) is required to build this application (packaged with AndroidStudio)
* A Proxy solution to hit vhapi in lower environments:
  * [Fiddler Classic](https://www.telerik.com/download/fiddler) for PCs
  * [ProxyMan](https://proxyman.com/download) for Macs or PCs
  * Instructions on setup (https://vaxcare.atlassian.net/wiki/spaces/CSP/pages/775455377/How+To+Set+Up+Fiddler+to+Proxy+as+a+Debugging+Proxy)
* Bridge App installed through Play store onto the device
  * This is only required so that the app can access the serial number of the device it is installed on
  * The Bridge App is signed by SocialMobile to have root access so that it can pass the serial number to this app
  * The app will run without this, but the serial number will be reported as "NO_PERMISSIONS"
* Keystore - In order to be able to sign locally different build variants we must copy locally 2 files:
  * [keystore.jks](https://vaxcare.sharepoint.com/:f:/r/sites/ProductTeam/Shared%20Documents/Mobile/3-HubDocumentation/VaxHubPlatformKeys?csf=1&web=1&e=EzPeSm)
  * [properties.gradle ](https://vaxcare.sharepoint.com/:f:/r/sites/ProductTeam/Shared%20Documents/Mobile/3-HubDocumentation/VaxHubPlatformKeys?csf=1&web=1&e=EzPeSm)
  * Windows Target Folder: `C:\vaxcare\Tools\VaxHubPlatformKeys`
  * MacOS Target Folder: `/Users/[USER]/vaxcare/Tools/VaxHubPlatformKeys`

### Build and Test
The current Android Studio build variant must match whatever environment you specify as the testBuildType
in the `app/build.gradle` file in order to see the androidTest folder and run any of the automation tests within
* Debug - This runs the app/tests against our dev environment
* QA (not yet set up) - This runs the app/tests against our QA environment
* Staging (not yet set up) - This runs the app/tests against our Staging environment
* Release - This runs the app/tests against our Production environment
* Local (not yet set up) - This runs the app/tests against our MockWebServer implementation (used only for automation tests)

### Mock Data
TBD - This will likely be set up as a module rather than in a separate repository as it is for Hub & MH

---

## Emulator Configuration
TBD - add emulator specs for T105 & C6

## Versioning
TBD - in previous apps this was drive by the Go pipeline builds and consumed by the build.gradle file.
We will not be relying on Go for this repository, so a GitHub solution will need to be implemented.

## Logging
* We use [Timber](https://github.com/JakeWharton/timber?tab=readme-ov-file#readme) for logging
* We output our Timber logs to [MixPanel](https://mixpanel.com/project/1358197/view/35297/app/events) for business analysis reports
* We output our Timber logs to [DataDog](https://us3.datadoghq.com/rum/sessions) for triaging issues

## Code Style: ktlint
This project uses [ktlint](https://ktlint.github.io/), an anti-bikeshedding Kotlin linter with a built-in formatter, to enforce a consistent code style.  Ktlint helps us avoid style debates and maintain clean, readable code across the project.

### What ktlint does:**
* **Checks code style:** ktlint analyzes your Kotlin code and identifies style violations based on the official Kotlin coding conventions.
* **Auto-formats code:**  It can automatically fix many of the identified style issues, saving time and effort.
* **Configurable rules:** While primarily based on the Kotlin standard, with a few inactive rules voted by the team.

### How to use ktlint in this project:**
* **Running ktlint manually:** ktlint can be run from the command line:

``` bash
./gradlew ktlintCheck # Checks for style violations
./gradlew ktlintFormat # Formats the code to fix violations
```
Please make sure these two commands are run and passed before creating a PR with your code changes.## Running automation tests

## Firebase integration
TBD - We need to hook this app up to the same firebase projects used by the Hub & MH and download the
google-services.json file from there and add it to our app to finish this setup.

There are 4 firebase projects, one for each environment:
* [Vaxhubs-Dev](https://console.firebase.google.com/project/vaxhubs-dev/overview) for the Dev environment
* [Vaxhubs-QA](https://console.firebase.google.com/project/vaxhubs-qa/overview) for the QA environment
* [Vaxhubs-Stg](https://console.firebase.google.com/project/vaxhubs-stg/overview) for the Staging environment
* [Vaxhubs-Prod](https://console.firebase.google.com/project/vaxhubs-prod/overview) for the Production environment

---

## App Architecture
Our architecture is based on the google recommendations:
* [Layered architecture](https://developer.android.com/topic/architecture).
* [Modularization](https://developer.android.com/topic/modularization)

We implement the following libraries:
* [Kotlin](https://kotlinlang.org/) language
* [Hilt](https://dagger.dev/hilt/) dependency injection
* [Gradle](https://gradle.org/) build tool
* [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) architecture pattern
* [KSP](https://kotlinlang.org/docs/ksp-overview.html) annotation processor
* [Retrofit](https://square.github.io/retrofit/) http client
* [Moshi](https://github.com/square/moshi) json parsing
* [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for storing long-lived configuration settings
* [Room](https://developer.android.com/training/data-storage/room) for storing long-lived data in a sqllite database
* [WorkManager](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started) for scheduling tasks for background processing
* [Compose](https://developer.android.com/compose) for UI rendering
* [Compose Navigation](https://developer.android.com/develop/ui/compose/navigation) for navigating between screens
* [Firebase Messaging](https://firebase.google.com/docs/cloud-messaging) for processing server messages
* [In App Updates](https://developer.android.com/guide/playcore/in-app-updates) for keeping our devices up to date
* [JUnit](https://junit.org/junit4/) testing framework
* [MockK](https://mockk.io/) for easy class mocking in unit tests
* [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) for responding to automation test web requests with mock data
* [Espresso](https://developer.android.com/training/testing/espresso) for automation testing

## 🏗️Core Base Classes

> The pieces below are the only boilerplate you need to write an MVI screen.
> Everything else is feature code.

### 1.Marker Interfaces

| Interface      | Role                                                                   |
|----------------|------------------------------------------------------------------------|
| `UiState`      | Immutable data rendered by the screen.                                 |
| `UiIntent`     | User/system wishes sent **to** the ViewModel.                          |
| `UiEvent`      | One‑shot effects emitted **from** the ViewModel (snackbar, nav, etc.). |
| `ActiveDialog` | Optional mix‑in that carries the currently displayed dialog key.       |

---

### 2.`BaseViewModel<S,E,I>`

*Generic ViewModel that every screen extends.*

* **Hot `StateFlow`** – kept alive while the screen is subscribed.  
  `start()` is invoked once a collector appears (instead of `init {}` for testability).
* **`setState { … }`** – atomic reducer; logs previous vs. new state via `Timber`.
* **`sendEvent(event)`** – fire one‑shot `UiEvent`s on a `SharedFlow`.
* **`currentState()`** – handy for assertions in tests.

``` kotlin
class MyViewModel @Inject constructor(...) :
    BaseViewModel<MyState, MyEvent, MyIntent>(MyState()) {

    override fun handleIntent(intent: MyIntent) { … }
    override fun start() { /* optional bootstrap */ }
}
```

### 3.BaseMviScreen

Bridges ViewModel ↔️ Compose.

``` kotlin
BaseMviScreen(
    viewModel = vm,
    onEvent = { event -> /* handle UiEvent */ }
) { state, sendIntent ->
    // Composable content here
}
```

- Collects **state** using `collectAsStateWithLifecycle` for lifecycle awareness.
- Collects **events** using `flowWithLifecycle`, ensuring only active collectors receive them.
- Uses `rememberUpdatedState` to always invoke the latest `onEvent` lambda.
- Exposes `sendIntent` to the Composable UI to dispatch intents.
- Fully test-friendly: avoids event loss or LaunchedEffect bugs on recomposition.

---

### 4.BaseViewModelTest
A lightweight, opinionated DSL for **unit-testing any ViewModel** that derives
from the project’s base MVI architecture.

``` kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest : BaseViewModelTest<MyState, MyEvent, MyIntent>() {

    override lateinit var viewModel: MyViewModel

    @Before
    fun setUp() {
        // Arrange mocks here…
        viewModel = MyViewModel(dispatchers)
    }

    // THEN · Snapshot test
    @Test fun `initial snapshot`() =
        thenStateShouldBe(MyState())

    // GIVEN–WHEN–THEN - uiState stream
    @Test fun `GIVEN loaded WHEN doSomething THEN updates state`() =
        whenState {
            skipItems(1) // 0 = initial, 1 = loaded
            viewModel.handleIntent(MyIntent.DoSomething)
            advanceUntilIdle() // drains scheduler
            assertEquals(
                MyState(result = "done"),
                awaitItem()
            )
        }

    // GIVEN–WHEN–THEN - uiEvent stream
    @Test fun `GIVEN data WHEN trigger THENe mits toast`() =
        whenEvent(
            action = {
                viewModel.handleIntent(MyIntent.Trigger)
                advanceUntilIdle()
            },
            assertions = {
                assertTrue(awaitItem() is MyEvent.Toast)
            }
        )
}
```

- Automatically includes:
  - `MainDispatcherRule` to replace Dispatchers.Main.
  - `TestDispatcherProvider` for controlling all coroutine execution.
  - Provides clean test helpers:
    - `givenState { … }`: Set an initial state without exposing setState() in every test.
    - `whenState { … }`: observe and assert `uiState` emissions.
    - `whenEvent { … }`: trigger actions and assert one-shot `uiEvent` emissions.
    - `thenStateShouldBe(expected)`: assert the current state snapshot.
- All tests use virtual time, are deterministic, and do not leak coroutines.

---

### ✅ TL;DR
1. Implement UiState, UiIntent, and UiEvent for your screen.
2. Extend BaseViewModel, override handleIntent() and optionally start().
3. Connect your ViewModel to Composables with BaseMviScreen.
4. Write clean and deterministic tests using BaseViewModelTest.

The architecture separates state, events, and side-effects.
You focus on behavior. The rest is boilerplate-free and test-friendly.

## VaxCare System Design
This application is currently using the [VaxCare Design System](https://www.figma.com/design/aTSgtiFGNCZOCQtKg2gxdR/Design-System-2025---Touch-Devices?node-id=0-1&p=f&m=dev),
that is provided as a CompositionLocal property as part of the 
VaxCareTheme Composable (theme Composable that wraps the entire app).

This system was created from our design team, following Material Design patterns like [tokens](https://www.figma.com/design/aTSgtiFGNCZOCQtKg2gxdR/Design-System-2025---Touch-Devices?node-id=4997-6682&vars=1&m=dev),
but written in the VaxCare way. Meaning that our components, 
are Material Design components but mapped to follow VaxCare design.

Here an example on how to use it:
``` kotlin
Column(  
    ...  
) {  
    Text(  
        text = "Hello $name!",  
        style = VaxCareTheme.type.displayTypeStyle.display3, // Here the VaxCare type subsystem is used
    )  
    Text(  
        text = "I am $serialNumber.",  
        style = VaxCareTheme.type.bodyTypeStyle.body5Italic,  
    )  
    Button(  
        modifier = Modifier,  
        colors = ButtonColors(  
            // Here the VaxCare color subsystem is used
            containerColor = VaxCareTheme.color.container.secondaryContainer,  
            contentColor = VaxCareTheme.color.onContainer.primaryInverse,  
            disabledContainerColor = VaxCareTheme.color.container.primaryPress,  
            disabledContentColor = VaxCareTheme.color.onContainer.onContainerPrimary  
        ),  
        content = { Text("Test metrics") },  
        onClick = buttonOnClickListener  
    )  
  
    LargeFloatingActionButton(  
        containerColor = VaxCareTheme.color.container.tertiaryContainer, 
        // Here the VaxCare measurement subsystem is used
        shape = RoundedCornerShape(VaxCareTheme.measurement.radius.fabLarge),  
        onClick = {},  
        content = {  
            Icon(  
                imageVector = Icons.Default.Check,  
                ...  
            )  
        }  
    )  
}
```

As you can see, you have access to this VaxCareTheme object class from the Composables and is mean
to have all the elements our Design System has; so our components can be 1:1.

---

## 🧪 Instrumented Testing Infrastructure

This guide explains how to use the integrated testing infrastructure to write clear, stable, and maintainable instrumentation tests using Compose, Hilt, MockWebServer, and Test Robots.

---

### 📐 Project Structure Overview

| Layer            | Purpose                                                   |
|------------------|-----------------------------------------------------------|
| :app:test        | Entry point for all feature tests                         |
| core:testdata    | Shared JSON responses used across features                |
| core:testnetwork | Shared MockWebServer setup and URL wiring                 |
| core:datastore   | Replaced via TestDataStoreModule to avoid disk collisions |
| MainActivity     | Launch entry point in Compose for most tests              |

---

### 🧱 Base Test Architecture

Use VaxCareIntegrationTestRule to automatically chain required JUnit rules:

```
@get:Rule
val testRule = VaxCareIntegrationTestRule(this)
```

This sets up:

- Hilt injection (HiltAndroidRule)

- DataStore cleanup (DataStoreTestRule)

- WorkManager support with Hilt (HiltWorkManagerTestRule)

- MockWebServer (TestServerRule)

- Compose UI testing (createAndroidComposeRule<MainActivity>())


---

### 🧪 Basic Test Template

```
@HiltAndroidTest
class SampleFeatureTest {

    @get:Rule
    val rule = VaxCareIntegrationTestRule(this)

    private val robot = SampleRobot(rule)

    @Test
    fun successfulFlow_displaysSuccessScreen() = with(rule) {
        server.get("/api/feature/data") {
            bodyJson("feature/data_success_200.json")
        }

        startActivity<MainActivity>() // or use deep link

        robot
            .assertDataVisible("Sample Title")
            .clickConfirm()
    }
}
```

---

### 🤖 Robots

Use robots for fluent, readable test interactions:

```
class SampleRobot(rule: VaxCareIntegrationTestRule) : BaseRobot<SampleRobot>(rule.compose, rule.server) {

    fun assertDataVisible(text: String) = apply {
        compose.onNodeWithText(text).assertIsDisplayed()
    }

    fun clickConfirm() = apply {
        compose.onNodeWithTag(TestTags.KEY_PAD_CONFIRM_BUTTON).performClick()
    }
}
```

---

### 🧷 Test Tags (Accessibility & Robustness)

UI components must expose stable test tags via Modifier.testTag(TestTags.FOO_BAR)

Defined centrally in:

```
core/ui/TestTags.kt
```

This improves:

- Selector stability

- Test readability

- Avoiding flakiness due to text or layout changes

> ✅ Best practice: Always prefer testTag over text match when targeting elements.

---

### 🌐 MockWebServer DSL

Use TestServerRule for clean HTTP mocking:

```
@get:Rule val server = TestServerRule()

server.get("/api/ping") {
    bodyJson("setup/ping_204.json")
}
```

Stub methods:

- get(...)
- post(...)
- put(...)
- delete(...)
- patch(...)

All load bodies using:

`bodyJson("path/to/asset.json")`

---

## Mock Response Files: The Centralized `testdata` Library

To ensure our tests are consistent and easy to maintain, all mock server responses are stored in a central "library" located in the **`:core:testdata`** module, under `src/main/resources/`.

### 1. Folder Structure: Group by API Resource

We organize our mock files into folders based on the main **API resource** or entity they represent. This is our single source of truth for what a server response for a given resource should look like.

**DO NOT** group files by the feature or screen you are testing. The goal is to create a reusable library.

**Correct Structure:**
```
/resources/
├── auth/
├── setup/
├── inventory/
└── product/
```

-   A mock for `GET /api/inventory/lotnumbers` belongs in the `inventory/` folder.
-   A mock for `POST /api/setup/LocationData` belongs in the `setup/` folder.

### 2. File Naming Convention (Strict)

We use a strict and descriptive naming convention to make the purpose of each file immediately obvious.

**Format:** `action_statusCode[_qualifier].json`

#### **`action` (Required)**
A lowercase verb that describes the business operation. This is the most important part of the name. We use a preferred set of verbs:

-   `get`: For fetching a single item (e.g., `GET /resource/{id}`).
-   `list`: For fetching a list of items (e.g., `GET /resource`).
-   `create`: For creating a new resource (e.g., `POST /resource`).
-   `update`: For updating an existing resource (e.g., `PUT` or `PATCH /resource/{id}`).
-   `delete`: For deleting a resource.

#### **`statusCode` (Required)**
The HTTP status code returned by the mock.

-   *Examples:* `200`, `201`, `401`, `404`, `500`.

#### **`_qualifier` (Optional)**
A `camelCase` tag for specific variations of the same response. This is used when the same action and status code can result in different data states.

-   *Examples:* `_emptyList`, `_invalidPassword`, `_withExpiredItem`, `_checkoutComplete`.

---
### Putting It All Together: Examples

**Scenario 1: Simple GET Request**
-   **Endpoint:** `GET /api/inventory/lotInventory`
-   **Location & Name:** `inventory/list_200.json`
-   **Usage in Robot:** `server.bodyJson("inventory/list_200.json")`

**Scenario 2: GET vs. PUT on the Same Endpoint**
-   **Endpoint:** `GET /api/appointment/123`
-   **Location & Name:** `appointment/get_200.json`
-   **Usage in Robot:** `server.bodyJson("appointment/get_200.json")`

-   **Endpoint:** `PUT /api/appointment/123`
-   **Location & Name:** `appointment/update_200.json`
-   **Usage in Robot:** `server.bodyJson("appointment/update_200.json")`

**Scenario 3: Failure on an Endpoint**
-   **Endpoint:** `GET /api/setup/ValidatePassword?password=...`
-   **Location & Name:** `auth/validatePassword_401_invalid.json`
-   **Usage in Robot:** `server.bodyJson("auth/validatePassword_401_invalid.json")`

**Scenario 4: Response with a Qualifier**
-   **Endpoint:** `GET /api/inventory/product/v2`
-   **Case 1: Standard response with items**
  -   **File:** `product/list_200.json`
-   **Case 2: Response with an empty list of items**
  -   **File:** `product/list_200_emptyList.json`

---

### 🧼 DataStore Cleanup

DataStore is injected via test-specific TestDataStoreModule, using in-memory files like TEST_DEVICE_STORE. Cleaned after each test by DataStoreTestRule.

You must not use by preferencesDataStore in tests. Only use PreferenceDataStoreFactory.

---

### ⚙️ WorkManager Tests

HiltWorkManagerTestRule sets up WorkManager with SynchronousExecutor and Hilt injection.

This ensures reliable and synchronous execution of workers.

---

### 🚀 Launching Test Entry Point

Use Compose test rule:

`rule.compose.setContent { App() }`

Or directly:

```
// Already part of VaxCareIntegrationTestRule
startActivity<MainActivity>() 
```

---

### 🧭 Optional: FCM/Background Test Setup (**Will be covered in a future PR!**)

Tests that simulate background events (e.g. FCM) should inject fake dependencies or use a FakeFirebaseService exposed to the test environment.

---

### 🧠 Quick Reference

| Task                    | How                                   |
|-------------------------|---------------------------------------|
| Replace Main dispatcher | CoroutineTestRule                     |
| Inject WorkManager      | HiltWorkManagerTestRule               |
| Clear DataStore         | DataStoreTestRule                     |
| Stub network            | `server.get { bodyJson(...) }`        |
| Add test tag            | `Modifier.testTag(TestTags.FOO_BAR)`  |
| Read asset              | `bodyJson("feature/foo_200.json")`    |
| Compose node            | `compose.onNodeWithTag(TestTags.XYZ)` |
| Reuse logic             | Use BaseRobot pattern                 |
