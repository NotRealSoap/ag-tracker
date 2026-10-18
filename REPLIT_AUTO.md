# Replit Auto-Compile - 3 Minutes

## Do This:

1. **Go to https://replit.com** and click "Create"
2. **Choose Java** as language
3. **In the file explorer on left**, create these folders:
   - `src/main/java`
   - `build/classes`

4. **Upload files** (drag & drop into replit):
   - Entire `src/main/java/org` folder from your project
   - `autogrinder.jar` file (from replit-build folder)

5. **Create a new file called `build.sh`** with this content:

```bash
#!/bin/bash
cd src/main/java
javac -encoding UTF-8 \
  -d ../../build/classes \
  -cp "../../autogrinder.jar" \
  org/polyfrost/example/module/modules/StatTracker.java \
  org/polyfrost/example/ui/StatScoreboard.java \
  org/polyfrost/example/config/TrackerCommand.java

if [ $? -eq 0 ]; then
    echo "✅ SUCCESS! Classes in build/classes/"
    echo "Download the build folder and send to Claude"
else
    echo "❌ Failed"
    exit 1
fi
```

6. **In replit terminal, run:**
```
bash build.sh
```

7. **Download** the `build/classes` folder
8. **Send it to me** and I'll inject it into your JAR

---

**That's it!** Should take 3-5 minutes total. Go! 🚀
