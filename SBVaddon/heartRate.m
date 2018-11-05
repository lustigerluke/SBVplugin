
clc
clear all
close all

'load files from '
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBVaddon/testData/*.txt'
sampling_frequnecy = 10;


files = glob(folderPath)
for i=1:numel(files)
  [~, name] = fileparts (files{i});
  ## eval(sprintf('%s = load("%s", "-ascii");', name, files{i}));
  
  data = load(files{i}); #load data
  # remove header rows
  tInd = data(4:length(data),1); #time index
  plainSignal = data(4:length(data),3);
  
  #calculate resonanz to get an idea of the heart rate. 
  fftData = fft(plainSignal);
  find(max(fftData(5:length(fftData)-5)))
  
  fftTime = tInd(1:1000);
  fftData = fftData(1:1000);
  sampling_frequency = 1000;
  fftFreq(i) =(i-1)*sampling_frequency/length(fftData);
  
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
  plot(fftFreq,fftData,"-b;fft;");


end