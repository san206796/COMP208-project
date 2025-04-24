# COMP208-project
## MerBusApp

An Android application that displays bus routes in Liverpool with estimated travel durations using Google Maps and integrates location services for a better user experience.

## Features

- **Interactive Bus Route Map**: Displays predefined bus routes with markers for start and end locations.
- **Route Selection**: Users can select from different bus routes (52A, 7, 86A, 10A, 1) using a spinner.
- **Estimated Travel Duration**: Displays estimated journey time based on the distance between the start and end points.
- **User Location**: Detects the user’s location and shows it on the map.
- **Bus Route Polyline**: Draws a polyline connecting the bus route's start and end locations.

## Installation

1. Clone the repo:

   git clone https://github.com/san206796/COMP208-project.git

2.Open the project in Android Studio.

3.Ensure that you have Google Play Services and Google Maps SDK enabled in your Google Cloud Console.

4.Add your Google Maps API Key to the AndroidManifest.xml:
 
 /// xml
  <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE"/>///

5.Make sure the following permissions are set in AndroidManifest.xml:

///  xml
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>///

6.Run the app on:
Android Emulator (with Google Play Services enabled), or A physical Android device

7.Requirements:
Android Studio (Hedgehog or newer recommended)
Google Maps SDK for Android (enabled via Google Cloud Console)
Google API Key for Maps access
Ensure that location permissions are granted on the device or emulator.

8.Usage:
Launch the app.
Select a bus route from the dropdown spinner (e.g., "52A", "7").
The map will update with markers and a polyline representing the bus route.
The estimated travel time will be displayed below the spinner.

9.Contributors:
Shivansh
Raid
Alankrit
Sanjose
Amir


