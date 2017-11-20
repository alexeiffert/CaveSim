JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	CaveSim.java \
	ClickPanel.java \
	Cutscene.java \
	ImageFrame.java \
        Menu.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
