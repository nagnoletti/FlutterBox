flutter build ios-framework
rm -rf ../IOSApp/Flutter/Debug
rm -rf ../IOSApp/Flutter/Profile
rm -rf ../IOSApp/Flutter/Release
mv build/ios/framework/* ../IOSApp/Flutter/
