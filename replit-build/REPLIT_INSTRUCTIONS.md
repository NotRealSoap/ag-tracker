# Quick Compile on Replit

## Step 1: Go to https://replit.com
- Click "Create" → Choose "Java"
- Name it "AutoGrinder-Tracker"

## Step 2: Upload Files
- Drag and drop the `src/main/java` folder into replit
- Also upload the `autogrinder.jar` file

## Step 3: Create Compile Script
Create a file called `compile.sh`:

```bash
#!/bin/bash
mkdir -p build/classes
cd src/main/java

# Compile the tracker modules
javac -encoding UTF-8 \
  -d ../../build/classes \
  -cp "../../autogrinder.jar" \
  org/polyfrost/example/module/modules/StatTracker.java \
  org/polyfrost/example/ui/StatScoreboard.java \
  org/polyfrost/example/config/TrackerCommand.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "Classes compiled to: build/classes"
else
    echo "❌ Compilation failed"
    exit 1
fi
```

## Step 4: Run It
In replit terminal:
```
bash compile.sh
```

## Step 5: Download
Your compiled classes will be in `build/classes/`

---

Done! Then send me the compiled files and I'll inject them into your JAR.
