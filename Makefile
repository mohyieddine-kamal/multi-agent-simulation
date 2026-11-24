# Example de makefile pour compiler le squelette de code distribué
# Vous pouvez compléter ce makefile, mais étant donnée la taille du projet, 
# il est FORTEMENT recommandé d'utiliser un IDE!

# Organisation:
#  1) Les sources (*.java) se trouvent dans le répertoire src
#     Les classes d'un package toto sont dans src/toto
#     Les classes du package par defaut sont dans src
#
#  2) Les bytecodes (*.class) sont générés dans le répertoire bin
#     La hiérarchie des sources (par package) est conservée.
#
#  3) Une librairie gui.jar est distribuée pour l'interface grapique. 
#     Elle se trouve dans le sous-répertoire lib.
#
# Compilation:
#  Options de javac:
#   -d : répertoire dans lequel sont générés les .class compilés
#   -sourcepath : répertoire dans lequel sont cherchés les .java
#   -classpath : répertoire dans lequel sont cherchées les classes compilées (.class et .jar)

# --- Variables ---
BIN_DIR = bin
SRC_DIR = src
LIB_DIR = lib


GUI_JAR = $(LIB_DIR)/gui.jar
JFLAGS = -d $(BIN_DIR) -classpath $(GUI_JAR) -sourcepath $(SRC_DIR)

# --- Commandes de compilation ---

all: compile

compile:
	@mkdir -p $(BIN_DIR)
	javac $(JFLAGS) $(SRC_DIR)/*.java

# --- Commandes pour executer ---

runBalls: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestBallsSimulator

runBoids: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestBoidsSimulator

runBehaviour: compile
	java -classpath $(BIN_DIR):$(GUI_JAR)  TestBehaviour

runJeuDeLaVie: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestJeuDeLaVie

runImmigration: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestJeuImmigration

runSchelling: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestSchelling

runInvader: compile
	java -classpath $(BIN_DIR):$(GUI_JAR) TestInvader

# --- Commandes nettoyage ---
clean:
	rm -rf $(BIN_DIR)