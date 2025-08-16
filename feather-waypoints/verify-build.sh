#!/bin/bash
echo "=== ATPoint Mod Build Verification ==="
echo "Author: Mr.Aayush Bhandari"
echo ""

echo "✅ 1. Project Structure Check:"
echo "   - Java source files:"
find src -name "*.java" | wc -l | xargs echo "     Java files found:"
echo "   - Resources:"
find src -name "*.json" -o -name "*.png" | wc -l | xargs echo "     Resource files found:"

echo ""
echo "✅ 2. Mod Metadata Check:"
echo "   - Mod name: $(grep '"name"' src/main/resources/fabric.mod.json | cut -d'"' -f4)"
echo "   - Author: $(grep -A 1 '"authors"' src/main/resources/fabric.mod.json | tail -n 1 | cut -d'"' -f2)"
echo "   - Archive name: $(grep 'archives_base_name' gradle.properties | cut -d'=' -f2)"

echo ""
echo "✅ 3. Logo Check:"
if [ -f "src/main/resources/assets/featherwaypoints/icon.png" ]; then
    echo "   - Logo file exists: $(ls -lh src/main/resources/assets/featherwaypoints/icon.png | awk '{print $5}')"
else
    echo "   - ❌ Logo file missing"
fi

echo ""
echo "✅ 4. Basic Java Syntax Check:"
java_error=0
for java_file in $(find src -name "*.java"); do
    if ! javac -cp "." -d /tmp/check "$java_file" 2>/dev/null; then
        echo "   - ❌ Syntax error in: $java_file"
        java_error=1
    fi
done

if [ $java_error -eq 0 ]; then
    echo "   - ✅ All Java files have valid syntax"
fi

echo ""
echo "✅ 5. Gradle Setup Check:"
if [ -f "gradlew" ] && [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "   - ✅ Gradle wrapper ready"
else
    echo "   - ❌ Gradle wrapper incomplete"
fi

echo ""
echo "=== Build Ready Status ==="
if [ $java_error -eq 0 ]; then
    echo "🎯 ATPoint mod is ready for building!"
    echo "📦 Run: ./gradlew build (may take several minutes for first build)"
    echo "🚀 JAR will be created as: build/libs/atpoint-1.0.0.jar"
else
    echo "❌ Fix Java syntax errors before building"
fi

echo ""
echo "Created by Mr.Aayush Bhandari with ❤️"
