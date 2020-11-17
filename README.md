# RacletteDB
## Students
 - David Crittin
 - Sylvain Meyer
## Description
This application has been developped during our Android & Cloud development course. Its goal is to take in hand the Android development through a practical project.

# Useful informations
### Admin password
    BestCheeses

### Remove a cheese / shieling
When displaying the cheeses / shielings list, long press on the desired item. Be aware when removing a shieling, all of its related cheeses will be deleted.

### Remove a cheese / shieling picture
Long press on the picture while in edit mode

### Edit a shieling location
Long press on the marker, then move at wanted location

# Project goals
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
