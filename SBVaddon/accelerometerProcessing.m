
clc
clear all
close all

pkg load io

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/'
fileName = 'acceleration.csv';
filePath = [folderPath, fileName]

A = xlsread(filePath, 'tisch2m', 'A2:B577');

B(1) = 0;
N = length(A(:,1));
for j=2:N
  B(j) = A(j,2) + B(j-1);   
endfor


C(1) = 0;
for j=2:N
  C(j) = B(j) + C(j-1);   
endfor



figure()
subplot(2,1,1);
xlabel("time in ms");
ylabel("acc in m/s^2");
hold on
plot(A(:,1),A(:,2),"-b;acceleration;");
plot(A(:,1),B,"-;velocity;")
subplot(2,1,2);
xlabel("time in ms");
ylabel("way");
plot(A(:,1),C,"-b;distance;")