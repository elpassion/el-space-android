#!/usr/bin/env bash
echo no | android create avd --force -n test -t android-19 --abi armeabi-v7a --skin 480x800
emulator -avd test -no-audio -no-window &
android-wait-for-emulator
adb shell settings put global window_animation_scale 0 &
adb shell settings put global transition_animation_scale 0 &
adb shell settings put global animator_duration_scale 0 &
sleep 28
adb shell input keyevent 82