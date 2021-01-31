# Welcome to RedDrip!

This application was developped as a final assignement for the developping application course at CYU and of course RedDrip is a **Non Profit** application! its main purpose is to save as many lives as it can!

## The application

This application give the opportunity to its users to search and give blood, create announcement for blood donations and search, many options were created by the developpers to facilitate the use of the application, and optimize the number of charitable actions. It was developped using Android studio (Java) and uses censors, maps, notifications and calls. to save the user's data, it uses in addition to shared preferences  an external real-time database (Firebase). to ensure a quality service, this application is based on 7 activities:


#### SplashActivity

This is the starting activity of this application, it show our logo and load the shared preferences of the user, based on them, it either start the login activity if they're false or inexistant, or the acceuil activity if the stored informations belongs to a registred user.
 
#### Register

On this activity the new user type his personnal informations with which a new account will be created in the database. The user can then connect to his freshly created account.  

#### Login

This activity is like a guard of the application, based on the enterred informations by the user, it either reconize and connect him, or stop him on the door until he gives valid informations. 

#### MapsActivity

This activity is a map in which we can choose an adress and it will display it on the screen (by default the adress is the user current adress), the activity is used when registering or creating a new donation/search to have a correct adress.

#### Acceuil

Acceuil is basically an activty made of three buttons which starts activities : UserProfile, Recherche and Login ( this one also delete the shared preferences ) but thanks to Firebase and its real-time functions we can inflate an empty layout in the activity and that in almost real time conditions so the user has always the latest announcements.

#### Recherche

Recherche is the activity that allow us to create new announcements, it is basically a form in which we type the announcement informations and its adress ( which require launching MapsActivity ) which are sent to the database.

#### UserProfile

UserProfile is the client's history center : from there he can check his announcement ( which are generated dynamically ).
<br>
<br>
On top of these seven activities there are four other crucial component to explain : 
<br>
#### AnnoncesProches

This service is launched after we get connected by the SplashActivity using shared preferences, it search for all blood searchs in a 10 km radius and if the user is compatible it will send him a notification.

#### Activity_user_profile3.xml

This layout is important because it is the layout we use to fill Acceuil activity, it is composed of multiple textviews in which we display the announcement informations, an imageView to specify the annoucement type ( it s either a search or a donnation ) and two buttons : 
 1. "Appeler" wich calls the number introduced on the announcement creation by the user who created it.
 2. "Afficher sur la carte" which opens google maps and show us the adress entered by the user who created the announcement.

#### Activity_cards.xml

This layout is the one we use to fill UserProfile activity, it has a textView in which we display the description of the annoucement, an imageView for the type, and a button to remove the announcement, this button remove of course the announcement from both the layout and the database.

#### AndroidManifest.xml

Aaah the Manifest!  We can't write a readme file without speaking about it.  Of course it contains all our activities and services, but on top of that it is important to not forget the google maps api key, and the permissions.
If those aren't correctly set up correctly the applications won't work!
