ALL = clean BDetection run

CC=javac
ENGINE=java
JFLAGS=-deprecation -Xlint

all: $(ALL)

BDetection: BDetection.java
	@echo "-------------------------------"
	$(CC) $(JFLAGS) detection/$^
	@echo "-------------------------------"

run:
	@echo "running..."
	$(ENGINE) detection.BDetection
	@echo "finished running."

BDetection.java:

clean:
	@echo "-------------------------------"
	@echo "*** Cleaning Files..."
	rm -f detection/*.class
	rm -f detection/*/*.class
	@echo "-------------------------------"
