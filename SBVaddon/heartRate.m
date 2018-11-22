
clc
clear all
close all

pkg load financial

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/*.txt'
SAMPLING_FREQUENCY = 1000;

files = glob(folderPath)
#for i=1:2 # for debugging
for i=1:numel(files)
  [~, name] = fileparts (files{i});
  ## eval(sprintf('%s = load("%s", "-ascii");', name, files{i}));
  
  # read file
  data = load(files{i}); #load data
  
  # remove header rows
  tIndex = data(4:length(data),1); #time in ms
  t = (1/SAMPLING_FREQUENCY)*(1:length(tIndex)); # time in s
  plainSignal = data(4:length(data),3);
  
  # moving average to remove offset
  wndw = length(plainSignal)/10;            %# sliding window size
  meanVec = movavg(plainSignal,wndw-1,wndw);%# moving average
  resultSignal = plainSignal - meanVec;
  
  # moving average as a filter
  wndw = 2;            %# sliding window size
  resultSignal = movavg(resultSignal,wndw-1,wndw);%# moving average
  
  #calculate resonanz to get an idea of the heart rate.  
  y_fft = abs(fft(resultSignal));
  y_fft = y_fft(1:length(y_fft)/4); % discard useless points
  f = SAMPLING_FREQUENCY * (0:length(resultSignal)/4-1)/length(resultSignal);
  # remove first data as 0 is main frequency

  
  fig1 = figure();
  subplot(2,2,1);hold on; # first subplot
  title(name)
  xlabel("time in ms");
  ylabel("ch1");
  grid on
  plot(tIndex,plainSignal,"-b;plain data;");
  subplot(2,2,2);hold on; # second subplot
  xlabel("amp");
  ylabel("time in s");
  grid on
  plot(t,resultSignal,"-b;filtered data;");

  subplot(2,2,3);hold on; # second subplot
  xlabel("frequency");
  ylabel("amplitude");
  grid on
  plot(f,y_fft)

  
end