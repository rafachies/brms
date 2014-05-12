This an web application that holds a bpm engine using jBPM libraries of BRMS 5.3.1.
It exposes its features through REST services TaskWS and ProcessWS, and manage the human tasks with a Local handler.
For deploying we need to add two datasources on the application server, java:jboss/datasources/jbpmDS with JTA activated, and java:jboss/datasources/taskDS with no JTA.
The persistence.xml is configured with a MySQL dialect, and we also expect a system property defined in the application server with the key bpm.session.id and the value is the ID of the Knowledge Session to create/load from database
