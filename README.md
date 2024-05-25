### "# ASCII-art-converter" ### 

Introducing an ASCII art converter for images with a Shell interface as the main file,
Utilizing Java exceptions to handle incorrect user inputs ensures smooth interaction and error management throughout the program.
Offering a terminal-like experience with multiple options to manipulate ASCII art:

1. exit: Terminates the program.
2. chars: Displays the current stock of characters available for generating the art.
3. add: Adds characters to the character array, with options to add:
	a. add a single character: for example "add a"
	b. add all- adda all the charachters according to ASCII (' ' to '~')
	c. "add space"- adds the space charachter
	d. add range of charachters- for example "add m-p" will add p,o,n,m

4. remove: Eliminates a specific character from the array.
5. res (resolution): Controls the resolution of the art, defaulting to 128. "Res up" doubles the resolution, while "Res down" halves it.
6. image - Selects the desired image by providing the new image path.
7. output- Chooses the output type, either printing to the console or returning as an HTML file.
8. asciiArt: Executes the algorithm with the current settings.



