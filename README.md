# SalsaStep - Dance Practice Tracker

SalsaStep is an Android application designed to help salsa dancers track their practice sessions. It provides a gamified approach to practicing salsa by tracking steps, recording practice time, and allowing users to monitor their progress across different dance moves.

## Features

### Practice Tracking
- Step counter using device's accelerometer sensor
- Practice timer with pause/resume functionality
- Calendar view to track practice history
- Daily practice statistics including:
  - Total practice time (hours, minutes, seconds)
  - Total steps taken
  - Ability to delete records for specific dates

### Dance Move Library
- Comprehensive collection of salsa moves
- Categorized by difficulty level (Beginner, Intermediate, Difficult)
- Personal mastery rating system (5-star rating)
- Instructional video links for each move
- Progress tracking for individual moves

## Screenshots
[Add your app screenshots here]

## Technical Details

### Requirements
- Android Studio Arctic Fox or later
- Minimum SDK: Android 7.0 (API level 24)
- Target SDK: Android 14 (API level 34)
- Device with step counter sensor

### Libraries & Components Used
- AndroidX Components
- SQLite Database for practice session storage
- Step Counter Sensor API
- CalendarView for date tracking
- RecyclerView for move library
- Shared Preferences for ratings storage

### Key Components

#### Practice Mode
- Real-time step counting
- Timer with pause/resume functionality
- Daily practice history
- Statistics tracking and storage

#### Dance Move Library
- Move categorization
- Progress tracking
- Video integration
- Personal ratings system

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/SalsaStep.git
```

2. Open the project in Android Studio

3. Build and run the application

## Database Schema

### Practice Sessions Table
```sql
CREATE TABLE practice_sessions (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT NOT NULL,
    duration INTEGER NOT NULL,
    steps INTEGER NOT NULL
);
```

### Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/salsasteps/
│   │   │   ├── PracticeActivity.java
│   │   │   ├── LibraryActivity.java
│   │   │   ├── PracticeSessionDbHelper.java
│   │   │   └── ...
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_practice.xml
│   │       │   ├── activity_library.xml
│   │       │   └── item_dance_move.xml
│   │       └── ...
│   └── ...
└── ...
```

## Usage

1. **Practice Mode**
   - Click "Practice Now" on the main screen
   - Use Start/Pause/Stop buttons to control your practice session
   - View your practice history in the calendar view
   - Click on any date to see that day's practice statistics

2. **Dance Move Library**
   - Access the library from the main screen
   - Browse moves by difficulty
   - Rate your progress on each move
   - Watch instructional videos for moves

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

- Dance move tutorials sourced from various salsa instructors (links provided in app)
- Android step counter and sensor documentation
- Material Design guidelines for UI/UX
