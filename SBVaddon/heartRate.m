
clc
clear all
close all

pkg load financial

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/*.txt'
sampling_frequnecy = 10;


files = glob(folderPath)
for i=1:2 # for debugging
#for i=1:numel(files)
  [~, name] = fileparts (files{i});
  ## eval(sprintf('%s = load("%s", "-ascii");', name, files{i}));
  
  data = load(files{i}); #load data
  # remove header rows
  tInd = data(4:length(data),1); #time index
  plainSignal = data(4:length(data),3);
  
  #calculate mean
  wndw = length(plainSignal)/10;            %# sliding window size
  meanVec = movavg(plainSignal,wndw-1,wndw);%# moving average

  resultSignal = plainSignal - meanVec;
  
  
  
  
  fig1 = figure();
  subplot(2,1,1);hold on; # first subplot
  title(name)
  xlabel("time in ms");
  ylabel("ch1");
  grid on
  plot(tInd,plainSignal,"-b;plain data;");
  subplot(2,1,2);hold on; # second subplot
  xlabel("amp");
  ylabel("ch1");
  grid on
  plot(tInd,resultSignal,"-b;filtered data;");


end