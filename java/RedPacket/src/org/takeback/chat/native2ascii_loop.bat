for /r %%a in (*.java) do (
	native2ascii.exe -reverse -encoding UTF-8 %%a %%a.temp
del %%a
move %%a.temp %%a
)