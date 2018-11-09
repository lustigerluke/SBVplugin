
clc
clear all
close all

'load files from '
# constants
# please note if copy-pasting windows paths: there are no backslashes int he path 
folderPath = 'C:/Users/Lukas/Documents/Signal- und Bildverarbeitung/workspace/project/plugins/SBVplugin/SBV2/testData/'
sampling_frequnecy = 10;

searchPath = [folderPath , '*']
files = glob(searchPath)
for i=2:numel(files)
  'get current file name'
  [path, name] = fileparts (files{i});
  fullpath = files{i};
  fullpath(ismember(fullpath,['\'])) = '/'
  #data = load(files{i}); #load data 
  cd(folderPath);
  zip('1.zip',folderPath)
  
endfor

'SUCCESS'