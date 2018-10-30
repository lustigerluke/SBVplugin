
clc
clear all
close all

pkg load io

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/'
fileName = 'acceleration.csv';
filePath = [folderPath, fileName]

A = xlsread(filePath, 'tisch2m', 'A2:B577');

plot(A(:,1),A(:,2));