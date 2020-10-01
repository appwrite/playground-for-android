# playground-for-android
Simple examples that will introduce you with Appwrite + Android (=❤️)

**Work in Progress**

Appwrite playground for Android is a simple way to explore the Appwrite API & Appwrite Android SDK. Use the source code of this page to learn how to use different Appwrite Android SDK features.

## Get Started

You can learn how to integrate your Appwrite android SDK in your android project and see how different features of the SDK can be used.

This playground doesn't include any android and Appwrite best practices but rather intended to show the most simple examples and use cases of using the Appwrite API in your android application.

## System Requirements 
* JDK 9+
* Latest version of Android Studio.
* Make sure android studio is installed and that your android environment is set for running android projects.
* You have a project created in AppWrite using console and note down the project ID.
* Create the collection and documents under it. Assign the readable permission for both collection and documents .
* Note the colection ID and Document ID as it will be used later.
* Create a user in AppWrite using appwrite console and note down its email and password.  
 

## Java Dependencies Used

Apart from the AppWrite classes, this playground uses two open source dependencies, which are required AppWrite Classes.
* [Google GSON](https://github.com/google/gson) 

### Installation
1. clone this repository.
2. Open project in android studio.
3. Open  app/src/main/res/values/strings.xml file.
4. Update project_id, emdpoint, collectionID, user_email, user_password with details of the user account which you have created above.  
5. Click Run 'app' from Run Menu to execute the project. 
6. Press **LOGIN** button to Login and press **GET DATA!** to fetch the data
7. Pressing **LOGOUT** button will log you out and pressing **GET DATA!** will not fetch result.

## Contributing

All code contributions - including those of people having commit access - must go through a pull request and approved by a core developer before being merged. This is to ensure proper review of all the code.

We truly ❤️ pull requests! If you wish to help, you can learn more about how you can contribute to this project in the [contribution guide]([CONTRIBUTING.md](https://github.com/appwrite/appwrite/blob/master/CONTRIBUTING.md)).

## Security

For security issues, kindly email us [security@appwrite.io](mailto:security@appwrite.io) instead of posting a public issue in GitHub.

## Follow Us

Join our growing community around the world! Follow us on [Twitter](https://twitter.com/appwrite_io), [Facebook Page](https://www.facebook.com/appwrite.io), [Facebook Group](https://www.facebook.com/groups/appwrite.developers/) or join our Discord Server [Discord community](https://discord.gg/GSeTUeA) for more help, ideas and discussions.  
