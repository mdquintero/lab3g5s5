with open("archivo1.txt", "w") as file:
    for i in range(100000):
        file.write("{:09d}\n".format(i))
