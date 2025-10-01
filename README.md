# MagicDress

MagicDress is an Android application that controls interactive LED wearables through Bluetooth Low Energy (BLE). The app allows users to control both a "magic wand" and a "magic dress" with various lighting effects and animations.

## Concept

This application provides wireless control over wearable LED devices using Bluetooth connectivity. Users can trigger different lighting patterns and effects including:

- **Shimmer** - Shimmering light effects
- **Twinkle** - Twinkling star patterns (normal and light variants)
- **Lightning** - Lightning flash effects
- **Rainbow** - Rainbow color cycles (with and without pulsing)
- **Pulse** - Pulsing effects (all, left side, or right side)
- **White Slide** - Sliding white light patterns
- **Maize and Blue** - University of Michigan themed colors
- **Dark** - Turn off all lights
- **Mostly White** - Predominantly white lighting

The app also responds to motion gestures from the connected wand device, detecting movements like:
- Down motion
- Flat positioning
- Rotation
- Throwing motion

## Features

- Bluetooth Low Energy (BLE) connectivity
- Real-time lighting effect control
- Motion gesture detection
- Connection status monitoring
- Sound integration
- Interactive toggle button interface

## Requirements

- Android device with API level 23 (Android 6.0) or higher
- Bluetooth Low Energy (BLE) support
- Location permission (required for BLE scanning)
- Compatible LED wearable devices

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/MagicDress.git
   cd MagicDress
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Install on your Android device:
   ```bash
   ./gradlew installDebug
   ```

## Running the Application

1. **Grant Permissions**: When first launching the app, grant location permissions (required for Bluetooth LE scanning)

2. **Enable Bluetooth**: Ensure Bluetooth is enabled on your device

3. **Start Services**: Tap the "Start" button to begin scanning for and connecting to your LED wearable devices

4. **Control Effects**: Use the toggle buttons to select different lighting effects

5. **Monitor Status**: Check the connection status indicators to ensure your devices are connected

6. **Stop Services**: Tap the "Stop" button to disconnect and stop all services

## Development Setup

This project uses:
- **Gradle Build System**: Android Gradle Plugin 2.3.3
- **Target SDK**: Android 25 (Android 7.1)
- **Minimum SDK**: Android 23 (Android 6.0)
- **Build Tools**: 25.0.2

### Build Commands

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
```

## Architecture

The application follows a service-oriented architecture:

- **MainActivity**: Main UI controller with lighting effect buttons
- **BluetoothService**: Handles BLE connection and communication
- **SoundService**: Manages audio feedback
- **WandActions/DressActions**: Define action constants and broadcast receivers
- **MDApplication**: Application class for global state management

## Permissions

The app requires the following permissions:
- `INTERNET` - Network access
- `ACCESS_NETWORK_STATE` - Network state monitoring
- `BLUETOOTH` - Bluetooth access
- `BLUETOOTH_ADMIN` - Bluetooth administration
- `ACCESS_COARSE_LOCATION` - Required for BLE scanning

## License

This project is available under the standard terms. Please check with the repository owner for licensing details.