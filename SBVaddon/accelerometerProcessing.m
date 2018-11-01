
clc
clear all
close all

pkg load io

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/';
fileName = 'acceleration.csv';
filePath = [folderPath, fileName]

acceleration = xlsread(filePath, 'tisch2m', 'A2:B577');

'process plain data'
velocity(1) = 0; # assume the velocity to be zero at the beginning
N = length(acceleration(:,1));
for j=2:N
  velocity(j) = acceleration(j,2) + velocity(j-1);   
endfor

way(1) = 0; # assume the way to be zero at the beginning
for j=2:N
  way(j) = velocity(j) + way(j-1);   
endfor

'plot plain data'
fig1 = figure();
subplot(2,1,1);
xlabel("time in ms");
ylabel("acceleration, velocity");
hold on
plot(acceleration(:,1),acceleration(:,2),"-r;acceleration in m/s^2;");
plot(acceleration(:,1),velocity,"-g;velocity in m/2;")
subplot(2,1,2);
xlabel("time in ms");
ylabel("way");
hold on
plot(acceleration(:,1),way,"-b;distance;")


'begin CORRECTION'
# ACCELERATION CORRECTION#############################################################################
time = acceleration(:,1);
correctedAcceleration = acceleration(:,2);
# ACCELERATION - floating mean filter
radius = 2;
mask = 2 * radius +1;
for j = (radius+1) : N- (radius+1)
  correctedAcceleration(j) = mean(correctedAcceleration(j-radius:j+radius));   
endfor

# ACCELERATION - threshold
THRESHOLD = 0.12;
for j = (radius+1) : N - (radius+1)
  meanValue = abs(mean(correctedAcceleration(j-radius:j+radius)));
  if (meanValue < THRESHOLD)
    correctedAcceleration(j) = 0;   
  endif
endfor
correctedAcceleration(1:radius) = correctedAcceleration(radius+1);
correctedAcceleration(N-radius:N) = correctedAcceleration(N-radius-1);

# VELOCITY CALCULATION
correctedVelocity(1) = 0; # assume the velocity to be zero at the beginning
N = length(time);
for j=2:N
  correctedVelocity(j) = correctedAcceleration(j) + correctedVelocity(j-1);   
endfor

# VELOCITY CORRECTION

# find indizes of actual movement in accelerator data
nonzeroIndizes = find(correctedAcceleration(:)); # find nonzero data
lastPrecedentZeroIndex = min(nonzeroIndizes) # get first nonzero index
lastNonZeroIndex = max(nonzeroIndizes) # get last nonzero index

# calculate line from first meaningful data to the last
k = correctedVelocity(length(correctedVelocity))/(lastNonZeroIndex - lastPrecedentZeroIndex);
d = - k * lastPrecedentZeroIndex;
correctedVelocity(1:lastPrecedentZeroIndex) = 0; # if there is no acceleration -> set velocity to 0
correctedVelocity(lastNonZeroIndex:N) = 0; # if ther is no acceleration -> set velocity to 0
for j=lastPrecedentZeroIndex:lastNonZeroIndex-1
  correctedVelocity(j) = correctedVelocity(j) + abs(k * j + d);   
endfor


correctedWay(1) = 0; # assume the way to be zero at the beginning
for j=2:N
  correctedWay(j) = correctedVelocity(j) + correctedWay(j-1);   
endfor


'plot result'
distance = max(way) 
correctedDistance = max(correctedWay)

figure()
subplot(2,1,1)
grid on
hold on
plot(time,acceleration(:,2),"-r;acceleration;");
plot(time,correctedAcceleration,"-b;corrected acceleration;");
plot(time,correctedVelocity(:),"-c;corrected velocity;");
subplot(2,1,2)
hold on
grid on
plot(time,way,"-c;distance;");
plot(time,correctedWay(:),"-r;corrected distance;");
legend("location","southeast");