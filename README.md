# 221501076-Mobile-Application-Development-Repo
This is a repository for AI19511 Mobile Application Development Laboratory for ML and DL Applications course.

The procedure for developing a simple Login Page is as follows.

Pre-requirements:

1. Download and install JDK (Java Development Kit). Set the environment variable for PATH too. [Note: This step is required if Java is the programming language to develop the application]
2. Download and install Android Studio. 
3. Launch the app to set up the emulator by creating a convenient Android Virtual Device.
4. Also set up the app to use JDK.

Procedure:
1. Create a new project by selecting 'Empty Views Activity'.
2. Name it "LoginPage" and choose Java as the language to finish the project set up.
3. Navigate to 'activity_main.xml' in the /app/res/layout directory.
4. Create a Relative Layout.
5. Add a TextView tag for the heading 'Login Page'.
6. Add 2 EditText tags, each for Username and Password.
7. Add a Button tag for the login action.
8. Create appropriate id's for the tags and adjust the layout with appropriate attributes.
9. Navigate to 'MainActivity.java' in the /app/java/com.example.loginpage directory.
10. Create objects for the EditText and Button tags respectively.
11. Set up OnClickListener for handling the login action on entering the username and the password.
12. Include appropriate Toast messages for the actions.
13. Debug, build and run the application on the emulator (selected AVD).
