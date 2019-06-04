def main():

    write_file = open('colleges_details.json', 'w', encoding='utf8')
    colleges = open('colleges.json', 'r', encoding='utf8')

    write_file.write("{\n")
  
    for line in colleges:
        write_file.write("\t{\n")
        if "name" in line:
            write_file.write("\t\t" + line.strip() + "\n")
        if "link" in line:
            write_file.write("\t\t" + line.strip() + "\n")
            write_file.write("\t},\n")

    write_file.write("\n}")
main()