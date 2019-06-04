import requests
import substring
import html2text
import re


def main():

    write_file = open('colleges.json', 'w', encoding='utf-8')
    write_file.write("[\n")

    # For the 1st page
    url = "https://www.4icu.org/reviews/index0001.htm"
    print("Working on:", url)
    response = requests.get(url)

    count = 1

    response_long = response.content[response.text.index("table table-hover text-left"):]
    ind = response_long.find("</table>".encode())
    main_str = response_long[0:ind]
    
    is_first = True
    count = scrape_read(write_file, main_str, is_first, count)
    is_first = False
    write_file.write("\n")


    # For the rest of the pages
    for x in range(25):
        url = "https://www.4icu.org/reviews/index{0}.htm".format(x+2)
        print("Working on:", url)
        response = requests.get(url)

        response_long = response.content[response.text.index("table table-hover text-left"):]

        ind = response_long.find("</table>".encode())
        main_str = response_long[0:ind]

        count = scrape_read(write_file, main_str, is_first, count)

    write_file.write("\n]")
    write_file.close()


def scrape_read(write_file, main_str, is_first, count):
    
    hold = False

    link_done = False
    name_done = False

    name_string = ""
    for word in main_str.split():

        # Name of the university
        if "<a" in str(word):
            hold = True
        if hold:
            if "</a>" in str(word):
                name_string += str(word.decode('utf-8'))
                name_long = substring.substringByChar(name_string[name_string.index("\">"):], startChar='>', endChar='<')
                name = html2text.html2text(name_long[1:-1]).replace('\n', ' ')

                name_string = ""
                hold = False
                name_done = True
            else:
                name_string += str(word.decode('utf-8') + " ")

        # Link for information about the university
        if "href" in str(word):
            if "\"" in str(word):
                link_long = str(word.decode('utf-8'))
                link_short = substring.substringByChar(link_long, startChar="/", endChar="m")
                link = "https://www.4icu.org" + link_short
                link_done = True

        if link_done and name_done:
            if is_first:
                write_file.write("\t{\n")
            else:
                write_file.write(",\n\t{\n")

            response = requests.get(link)
            response_long = response.text[response.text.index("itemprop=\"description\""):]
            description_long = substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1]
            description = html2text.html2text(description_long)

            response_long = response.text[response.text.index("abbr>"):]
            abr = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])

            response_long = response.text[response.text.index("foundingDate"):]
            founding_date = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])

            try:
                response_long = response.text[response.text.index("Motto"):]
                motto = html2text.html2text(substring.substringByChar(response_long[response_long.index("cite>"):], startChar='>', endChar='<')[1:-1])
            except:
                motto = ""

            try:
                response_long = response.text[response.text.index("Colours"):]
                colors = html2text.html2text(substring.substringByChar(response_long[response_long.index("td>"):], startChar='>', endChar='<')[1:-1])
            except:
                colors = ""

            response_long = response.text[response.text.index("itemprop=\"streetAddress\""):]
            street_address = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])

            response_long = response.text[response.text.index("itemprop=\"addressLocality\""):]
            address_locality = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])

            response_long = response.text[response.text.index("itemprop=\"postalCode\""):]
            postal_code = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])

            response_long = response.text[response.text.index("itemprop=\"addressRegion\""):]
            address_region = html2text.html2text(substring.substringByChar(response_long[response_long.index('>'):], startChar='>', endChar='<')[1:-1])
            country = html2text.html2text(substring.substringByChar(response_long[response_long.index("<br>"):], startChar='>', endChar='<')[1:-1])
            telephone = html2text.html2text(substring.substringByChar(response_long[response_long.index("telephone"):], startChar='>', endChar='<')[1:-1])
            fax = html2text.html2text(substring.substringByChar(response_long[response_long.index("faxNumber"):], startChar='>', endChar='<')[1:-1])

            response_long = response.text[response.text.index("\"sp sp-facebook16x16\""):]
            facebook = html2text.html2text(substring.substringByChar(response_long[response_long.index("ref=\"https"):].split()[0], startChar='h', endChar='\"')[:-1])
            if "facebook" not in facebook:
                facebook = ""

            response_long = response.text[response.text.index("\"sp sp-twitter16x16\""):]
            twitter = html2text.html2text(substring.substringByChar(response_long[response_long.index("ref=\"https"):].split()[0], startChar='h', endChar='\"')[:-1])
            if "twitter" not in twitter:
                twitter = ""
            
            response_long = response.text[response.text.index("\"sp sp-linkedin16x16\""):]
            linkedin = html2text.html2text(substring.substringByChar(response_long[response_long.index("ref=\"https"):].split()[0], startChar='h', endChar='\"')[:-1])
            if "linkedin" not in linkedin:
                linkedin = ""
            
            response_long = response.text[response.text.index("\"sp sp-youtube16x16\""):]
            youtube = html2text.html2text(substring.substringByChar(response_long[response_long.index("ref=\"https"):].split()[0], startChar='h', endChar='\"')[:-1])
            if "youtube" not in youtube:
                youtube = ""

            response_long = response.text[response.text.index("\"sp sp-instagram16x16\""):]
            instagram = html2text.html2text(substring.substringByChar(response_long[response_long.index("ref=\"https"):].split()[0], startChar='h', endChar='\"')[:-1])
            if "instagram" not in instagram:
                instagram = ""

            try:
                response_long = response.text[response.text.index("Other locations"):]
                other_locations = html2text.html2text(substring.substringByChar(response_long[response_long.index("td>"):], startChar='>', endChar='<')[1:-1])
            except:
                other_locations = ""


            write_file.write("\t\t \"pk\": \"" + str(count) + "\",\n")
            write_file.write("\t\t \"name\": \"" + clean_string(name) + "\",\n")
            write_file.write("\t\t \"link\": \"" + clean_string(link) + "\",\n")
            write_file.write("\t\t \"description\": \"" + clean_string(description) + "\",\n")
            write_file.write("\t\t \"acronym\": \"" + clean_string(abr) + "\",\n")
            write_file.write("\t\t \"foundingDate\": \"" + clean_string(founding_date) + "\",\n")
            write_file.write("\t\t \"streetAddress\": \"" + clean_string(street_address) + "\",\n")
            write_file.write("\t\t \"addressLocality\": \"" + clean_string(address_locality) + "\",\n")
            write_file.write("\t\t \"postalCode\": \"" + clean_string(postal_code) + "\",\n")
            write_file.write("\t\t \"addressRegion\": \"" + clean_string(address_region) + "\",\n")
            write_file.write("\t\t \"otherlocations\": \"" + clean_string(other_locations) + "\",\n")
            write_file.write("\t\t \"motto\": \"" + clean_string(motto) + "\",\n")
            write_file.write("\t\t \"colors\": \"" + clean_string(colors) + "\",\n")
            write_file.write("\t\t \"country\": \"" + clean_string(country) + "\",\n")
            write_file.write("\t\t \"telephone\": \"" + clean_string(telephone) + "\",\n")
            write_file.write("\t\t \"fax\": \"" + clean_string(fax) + "\",\n")
            write_file.write("\t\t \"facebook\": \"" + clean_string(facebook) + "\",\n")
            write_file.write("\t\t \"twitter\": \"" + clean_string(twitter) + "\",\n")
            write_file.write("\t\t \"linkedin\": \"" + clean_string(linkedin) + "\",\n")
            write_file.write("\t\t \"youtube\": \"" + clean_string(youtube) + "\",\n")
            write_file.write("\t\t \"instagram\": \"" + clean_string(instagram) + "\"\n")

            write_file.write('\t}')

            link_done = False
            name_done = False
            count += 1
    return count

def clean_string(str):
    return str.replace('\n', ' ').replace("\-", '-').replace("\"", "\\\"").replace("\.", ".").strip()

main()