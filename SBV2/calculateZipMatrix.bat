@echo off


echo loop
for /r %%x in (testData\*.txt) do (
   
   for /r %%y in (testData\*.txt) do (
      echo new
      echo %%y
      echo %%x

   )


)

echo success. 
pause  