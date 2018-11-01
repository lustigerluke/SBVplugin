
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
figure()
subplot(2,1,1);
xlabel("time in ms");
ylabel("acceleration, velocity");
hold on
plot(acceleration(:,1),acceleration(:,2),"-b;acceleration in m/s^2;");
plot(acceleration(:,1),velocity,"-;velocity in m/2;")
subplot(2,1,2);
xlabel("time in ms");
ylabel("way");
hold on
plot(acceleration(:,1),way,"-b;distance;")

# the way displayed is decreasing at the end but should be constant
# if the velocity would be zero, the way would be constant

