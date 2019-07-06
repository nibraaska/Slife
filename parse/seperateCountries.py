def main():
    fp = open("colleges.json", encoding="utf8")
    fp2 = open("collegesdatabase.json",'w', encoding='utf-8')

    json_item = ""
    country = ""
    first = True
    firstLoop = True
    firstList = True
    item_found = False
    countryList = []
    seperatedDict = {}

    for line in fp:
        if "{" in line:
            item_found = True
        if item_found:
            json_item += line
            if "\"country\"" in line:
                country = line[15:-3]
                if country not in countryList:
                    countryList.append(country)
        if "}" in line:

            seperatedDict.setdefault(country,[]).append((json_item))

            item_found = False
            json_item = ""
            country = ""


    fp2.write("{\n")
    fp2.write("\t\"Colleges\" : {\n")
    for key in sorted(seperatedDict):
        if first:
            fp2.write("\t\t\"" + key + "\" :\n \t\t\t{\n")

            firstLoop = True
            for item in seperatedDict[key]:
                if firstLoop:
                    name = item.split("\n")[1][12:-2].replace('$', ' ').replace('#', ' ').replace('[', ' ').replace(']', ' ').replace('/', ' ').replace('.', ' ')
                    if name == "":
                        name = "Empty"
                    for line in item.split("\n"):
                        if "{" in line:
                            fp2.write("\t\t\t\t\"" + name.strip() + "\" : {\n")
                        elif "}" in line:
                            fp2.write("\t\t\t" + "}")
                        else:
                            fp2.write("\t\t\t\t" + line.strip() + "\n")
                    firstLoop = False
                else:
                    name = item.split("\n")[1][12:-2].replace('$', ' ').replace('#', ' ').replace('[', ' ').replace(']', ' ').replace('/', ' ').replace('.', ' ')
                    if name == "":
                        name = "Empty"
                    for line in item.split("\n"):
                        if "{" in line:
                            fp2.write("\n,\t\t\t \"" + name.strip() + "\" : {\n")
                        elif "}" in line:
                            fp2.write("\t\t\t" + "}")
                        else:
                            fp2.write("\t\t\t\t" + line.strip() + "\n")
            
            fp2.write("\t\t}")
            first = False
        else:
            fp2.write(",\t\t\"" + key + "\" :\n \t\t\t{\n")

            firstLoop = True
            for item in seperatedDict[key]:
                if firstLoop:
                    name = item.split("\n")[1][12:-2].replace('$', ' ').replace('#', ' ').replace('[', ' ').replace(']', ' ').replace('/', ' ').replace('.', ' ')
                    if name == "":
                        name = "Empty"
                    for line in item.split( "\n"):
                        if "{" in line:
                            fp2.write("\t\t\t\t\"" + name.strip() + "\" : {\n")
                        elif "}" in line:
                            fp2.write("\t\t\t" + "}")
                        else:
                            fp2.write("\t\t\t\t" + line.strip() + "\n")
                    firstLoop = False
                else:
                    name = item.split("\n")[1][12:-2].replace('$', ' ').replace('#', ' ').replace('[', ' ').replace(']', ' ').replace('/', ' ').replace('.', ' ')
                    if name == "":
                        name = "Empty"
                    for line in item.split("\n"):
                        if "{" in line:
                            fp2.write("\n,\t\t\t \"" + name.strip() + "\" : {\n")
                        elif "}" in line:
                            fp2.write("\t\t\t" + "}")
                        else:
                            fp2.write("\t\t\t\t" + line.strip() + "\n")

            fp2.write("\t\t}")
    fp2.write("\n\t}")
    fp2.write(",\n\t \"CountryList\" : { \n")
    fp2.write("\t\t \"CountryList\" : [ \n")
    for country in countryList:
        if firstList:
            fp2.write("\t\t\t" + "\"" + country + "\"")
            firstList = False
        else:
            fp2.write(",\n\t\t\t" + "\"" + country + "\"")
    fp2.write("\n\t\t]\n")
    fp2.write("\t}\n")
    fp2.write("}")


main()