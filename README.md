# RacletteDB
## Students
 - David Crittin
 - Sylvain Meyer
## Description
This application has been developped during our Android & Cloud development course. Its goal is to take in hand the Android development through a practical project.

# Useful informations
### Admin password
`BestCheeses` by default (current value in Firebase console > Remote Config > `admin_password` parameter)

### Remove a cheese / shieling
When displaying the cheeses / shielings list, long press on the desired item. Be aware when removing a shieling, all of its related cheeses will be deleted.

### Remove a cheese / shieling picture
Long press on the picture while in edit mode

### Edit a shieling location
Long press on the marker, then move at wanted location

### Send a notification
When in admin mode, the "Send nofication" menu is available in the drawer. The user can then send a notification that will be displayed to **other** users.

# Project gaols - Firebase
## Initial scope
- [x] Room initial scope and existing nice to have
- [x] Firebase	integration:
	- [x] Synchronization mechanism of data onto the cloud with Firebase Realtime Database
## "Nice to have"
- [x] Other Firebase utilities :
	- [x] **Firebase Storage** : cheeses and shielings uploaded picture are stored in the Firebase Storage, therefore every user can see one element's picture 
	- [x] **Firebase Cloud Messaging** : the application can receive notification sent from the application itself, or from the Firebase console
	- [x] **Firebase Remote Config** : the admin password is set from the Firebase console

# Project goals - Room
## Initial scope
 - [x] User interface allowing to
	 - [x] Show the dataset information
	 - [x] Change/Delete this information
	 - [x] Add new data
 - [x] Navigation drawer
 - [x] Settings
	 - [x] About information (information about the App)
	 - [x] other useful settings : application language, database restoration
 - [x] Storing data on the phone using Google’s Room API (1st part of project)

## "Nice to have"
 - [x] Select picture from phone camera (if present) or gallery
 - [x] Integration of Google Maps SDK
 - [x] Firebase storage : currently off, through hard-coded boolean switch `CLOUD_ACTIVE`. We realized that cloud storage did not make sense without cloud database, so we turned it off for now. 
