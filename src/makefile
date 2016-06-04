JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSPATH = jython-standalone-2.7.0.jar
SOURCEPATH = .


CLASSES = \
		BufferPair.java \
		CircuitObject.java \
		Decoder.java \
		HTTPProxy.java \
		INetAddress.java \
		ListenThread.java \
		MessageObject.java \
		MessageType.java \
		OpenObject.java \
		Pair.java \
		ProxySocketReadThread.java \
		ProxySocketWriteThread.java \
		RelayObject.java \
		RoutingTable.java \
		ServerSocketStartupThread.java \
		SocketClientBuffers.java \
		SocketData.java \
		SocketList.java \
		SocketManager.java \
		SocketReadThread.java \
		SocketServerBuffers.java \
		SocketWriteThread.java \
		Startup.java \
		StreamListenThread.java \
		WriteToTorFromServerBufferThread.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class

