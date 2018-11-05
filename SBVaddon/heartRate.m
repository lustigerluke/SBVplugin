
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
  
  acceleration = load(files{i});

  'plot plain data'
  fig1 = figure();
  hold on;
  title(name)
  xlabel("time in ms");
  ylabel("ch1");
  grid on
  plot(acceleration(:,1),acceleration(:,3),"-b;plain data;");

end