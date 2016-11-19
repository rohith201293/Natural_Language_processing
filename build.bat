javac -d . nlp/*.java

jar cvf nlp.jar nlp/*.class
del nlp\*.class
pause