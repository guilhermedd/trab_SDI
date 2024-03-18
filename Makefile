# GNU Makefile
JAR=/usr/bin/jar
JAVA=/usr/bin/java
JAVAC=/usr/bin/javac

JF = #-Xlint
TARGET = MultiThreadChatServer.class MultiThreadChatClient.class clientThread.class

JFLAGS = -g 
.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $(JFLAGS) $*.java

CLASSES = \
	MulticastReceiver.java\
	MulticastSender.java

default: classes

classes: $(CLASSES:.java=.class)

JC = /usr/bin/javac
JFLAGS = #-Xlint
TARGET = MultiThreadChatServer.class MultiThreadChatClient.class clientThread.class

all: $(TARGET)

clientThread.class: clientThread.java
	$(JC) $(JFLAGS) clientThread.java 

MultiThreadChatServer.class: MultiThreadChatServer.java
	$(JC) $(JFLAGS) MultiThreadChatServer.java 

MultiThreadChatClient.class: MultiThreadChatClient.java
	$(JC) $(JFLAGS) MultiThreadChatClient.java 

clean:
	rm -f *~ $(TARGET)