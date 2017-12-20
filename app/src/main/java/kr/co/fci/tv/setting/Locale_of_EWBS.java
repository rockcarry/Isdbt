package kr.co.fci.tv.setting;

import kr.co.fci.tv.util.TVlog;

/**
 * Created by justin.song on 2015-10-28.
 */
public class Locale_of_EWBS {
    private int countryIndex;
    private String countryName;
    private int areaCode;
    private String localeArea;

    // // use capital letters
   private String area_of_Brazil[][] = {
            {"001","Rondônia"},{"002","Acre"},{"003","Amazonas"},{"004","Roraima"},{"005","Pará"},{"006","Amapá"},{"007","Tocantins"},{"008","Maranhão"},
            {"009","Piauí"},{"00a","Ceará"},{"00b","Rio Grande do Norte"},{"00c","Paraíba"},{"00d","Pernambuco"},{"00e","Sergipe"},{"00f","Alagoas"},
            {"010","Bahia"},{"011","Minas Gerais"},{"012","Espírito Santo"},{"013","Rio de Janeiro"},{"014","São Paulo"},{"015","Paraná"},{"016","Santa Catarina"},
            {"017","Rio Grande do Sul"},{"018","Mato Grosso do Sul"},{"019","Mato Grosso"},{"01a","Goiás"},{"01b","Distrito Federal"}
        };
   private  String area_of_CostaRica[][] = {
            {"065","San José"},{"066","Escazú"},{"067","Desamparados"},{"068","Puriscal"},{"069","Tarrazú"},{"06a","Aserrí"},
            {"06b","Mora"},{"06c","Goicoechea"},{"06d","Santa Ana"},{"06e","Alajuelita"},{"06f","Vázquez de Coronado"},
            {"070","Acosta"},{"071","Tibás"},{"072","Moravia"},{"073","Montes de Oca"},{"074","Turrubares"},{"075","Dota"},{"076","Curridabat"},{"077","Pérez Zeledón"},
            {"078","León Cortés Castro"},

            {"0c9","Alajuela"},{"0ca","San Ramón"},{"0cb","Grecia"},{"0cc","San Mateo"},{"0cd","Atenas"},{"0ce","Naranjo"},{"0cf","Palmares"},
            {"0d0","Poás"},{"0d1","Orotina"},{"0d2","San Carlos"},{"0d3","Alfaro Ruiz"},{"0d4","Valverde Vega"},{"0d5","Upala"},{"0d6","Los Chiles"},{"0d7","Guatuso"},

            {"12d","Cartago"},{"12e","Paraíso"},{"12f","La Unión"},{"130","Jiménez"},{"131","Turrialba"},{"132","Alvarado"},{"133","Oreamuno"},{"134","El Guarco"},

            {"191","Heredia"},{"192","Barva"},{"193","Santo Domingo"},{"194","Santa Bárbara"},{"195","San Rafael"},{"196","San Isidro"},{"197","Belén"},{"198","Flores"},
            {"199","San Pablo"},{"19a","Sarapiquí"},

            {"1f5","Liberia"},{"1f6","Nicoya"},{"1f7","Santa Cruz"},{"1f8","Bagaces"},{"1f9","Carrillo"},{"1fa","Cañas"},{"1fb","Abangares"},{"1fc","Tilarán"},
            {"1fc","Nandayure"},{"1fe","La Cruz"},{"1ff","Hojancha"},

            {"259","Puntarenas"},{"25a","Esparza"},{"25b","Buenos Aires"},{"25c","Montes de Oro"},{"25d","Osa"},{"25e","Aguirre"},{"25f","Golfito"},
            {"260","Coto Brus"},{"261","Parrita"},{"262","Corredores"},{"263","Garabito"},

            {"2bd","Limón"},{"2be","Pococí"},{"2bf","Siquirres"},{"2c0","Talamanca"},{"2c1","Matina"},{"2c2","Guácimo"},

            {"064","San José"},{"0c8","Alajuela"},{"12c","Cartago"},{"190","Heredia"},{"1f4","Guanacaste"},{"258","Puntarenas"},{"2bc","Limón"},{"3e8","Costa Rica"}
        };
   private String area_of_Japan[][] = {
           {"34d","All area"},{"5a5","Wide area of Kantō"},{"72a","Wide area of Chukyo"},{"8d5","Wide area of Kinki"},
           {"699","Tottori,Shimane area"},{"553","Okayama,Kagawa area"},
           {"16b","Hokkaido"},
           {"467","Aomori"},{"5d4","Iwate"},{"758","Miyagi"},{"ac6","Akita"},{"e4c","Yamagata"},{"1ae","Fukushima"},
           {"c69","Ibaraki"},{"e38","Tochigi"},{"98b","Gunma"},{"64b","Saitama"},{"1c7","Chiba"},{"aac","Tokyo"},{"56c","Kanagawa"},
           {"4ce","Niigata"},{"539","Toyama"},{"6a6","Ishikawa"},{"92d","Fukui"},{"d4a","Yamanashi"},{"9d2","Nagano"},{"a65","Gifu"},{"a5a","Shizuoka"},{"966","Aichi"},
           {"2dc","Mie"},{"ce4","Shiga"},{"595","Kyoto"},{"cb2","Osaka"},{"674","Hyogo"},{"a93","Nara"},{"396","Wakayama"},
           {"d23","Tottori"},{"31b","Shimane"},{"2b5","Okayama"},{"b31","Hiroshima"},{"b98","Yamaguchi"},
           {"e62","Tokushima"},{"9b4","Kagawa"},{"19d","Ehime"},{"2e3","Kochi"},
           {"62d","Fukuoka"},{"959","Saga"},{"a2b","Nagasaki"},{"8a7","Kumamoto"},{"c8d","Oita"},{"d1c","Miyazaki"},{"d45","Kagoshima"},{"372","Okinawa"}
   };

    private String area_of_Philippines[][] = {
            {"412","Caloocan"},{"422","Las Piñas"},{"423","Makati"},{"424","Malabon"},{"425","Mandaluyong"},{"426","Manila"},{"427","Malikina"},{"428","Muntinlupa"},
            {"429","Navotas"},{"42a","Parañaque"},{"42b","Pasay"},{"42c","Pasig"},{"42d","Pateros"},{"42e","Quezon City"},{"42f","San Juan"},{"430","Taguig"},{"431","Vlenzuela"},
            {"441","Abra"},{"442","Apayao"},{"443","Baguio"},{"444","Benguet"},{"445","Ifugao"},{"446","Kalinga"},{"447","Mountain Province"},
            {"461","Ilocos Norte"},{"462","Ilocos Sur"},{"463","La Union"},{"464","Pangasinan"},
            {"481","Batanes"},{"482","Cagayan"},{"483","Isabela"},{"484","Nueva Vizcaya"},{"485","Quirino"},{"486","Santiago"},
            {"4a1","Angeles"},{"4a2","Aurora"},{"4a3","Bataan"},{"4a4","bulacan"},{"4a5","Nueva Ecija"},{"4a6","Olongapo"},{"4a7","Pampanga"},{"4a8","Tarlac"},{"4a9","Zambales"},
            {"4c1","Batangas"},{"4c2","Cavite"},{"4c3","Laguna"},{"4c4","Lucena"},{"4c5","Quezon"},{"4c6","Rizal"},
            {"4e1","Marinduque"},{"4e2","Occidental Mindoro"},{"4e3","Oriental Mindoro"},{"4e4","Palawan"},{"4e5","Puerto Princesa"},{"4e6","Romblon"},
            {"501","Albay"},{"502","Camarines Sur"},{"503","Camarines Norte"},{"504","Catanduanes"},{"505","Masbate"},{"506","Naga"},{"507","Sorsogon"},
            {"621","Aklan"},{"622","Antique"},{"623","Bacolod"},{"624","Capiz"},{"625","Guimaras"},{"626","Iloilo"},{"627","Iloilo City"},{"628","Negros Occidental"},
            {"641","Bohol"},{"642","Cebu"},{"643","Cebu City"},{"644","Lapu Lapu"},{"645","Negros Oriental"},{"646","Siquijor"},
            {"661","Biliran"},{"662","eastern Samar"},{"663","Leyte"},{"664","Northern Samar"},{"665","Ormoc"},{"666","Samar"},{"667","Southern Leyte"},{"668","Tacloban"},
            {"821","Isabela City"},{"822","Zamboranga City"},{"823","Zamboranga del Norte"},{"824","Zamboranga del Sur"},{"825","Zamboranga Sibugay"},
            {"841","Bukidnon"},{"842","Cagayan de Oro"},{"843","Camiguin"},{"844","Iligan"},{"845","Lanao del Norte"},{"846","Misamis Occidental"},{"847","Misamis Oriental"},
            {"861","Compostela Valley"},{"862","Davao City"},{"863","Davao del Norte"},{"864","Davao del Sur"},{"865","Davao Oriental"},
            {"881","Cotabato"},{"882","Cotabato City"},{"883","General Santos"},{"884","Sarangani"},{"885","South Cotabato"},{"886","Sultan Kudarat"},
            {"8a1","Agusan del Norte"},{"8a2","Agusan del Sur"},{"8a3","Butuan"},{"8a4","Dinagat Islands"},{"a85","Surigao del Norte"},{"8a6","Surigao del Sur"},
            {"8c1","Basilan"},{"8c2","Lanao del Sur"},{"8c3","Maguindanao"},{"c84","Sulu"},{"8c5","Tawi Tawi"}
    };

    private String area_of_Ecuador[][] = {
            {"019","Camilo Ponce Enríquez"},{"029","Chordeleg"},{"04f","Cuenca"},{"039","El Pan"},{"044","Girón"},{"048","Guachapala"},{"049","Gualaceo"},
            {"077","Nabón"}, {"07e","Oña"},{"08e","Paute"},{"09b","Pucará"},{"0b2","San Fernando"},{"0c0","Santa Isabel"},{"0c8","Sevilla de Oro"},{"0cb","Sigsig"},

            {"017","Caluma"},{"025","Chillanes"},{"026","Chimbo"},{"035","Echeandía"},{"04d","Guaranda"},{"060","Las Naves"},{"0b6","San Miguel"},

            {"00c","Azogues"},{"013","Biblián"},{"01a","Cañar"},{"033","Deleg"},{"03c","El Tambo"},{"05c","La Troncal"},{"0d1","Suscal"},

            {"014","Bolívar"},{"041","Espejo"},{"06f","Mira"},{"074","Montúfar"},{"0b9","San Pedro de Huaca"},{"0c7","Tulcán"},

            {"003","Alausí"},{"023","Chambo"},{"02a","Chunchi"},{"02c","Colta"},{"030","Cumandá"},{"04b","Guamote"},{"04c","Guano"},{"086","Pallatanga"},{"093","Penipe"},{"0a9","Riobamba"},

            {"05b","La Maná"},{"061","Latacunga"},{"089","Pangua"},{"09f","Pujilí"},{"0ad","Salcedo"},{"0c6","Saquisilí"},{"0ca","Sigchos"},

            {"009","Arenillas"},{"00b","Atahualpa"},{"010","Balsas"},{"024","Chilla"},{"038","El Guabo"},{"050","Huaquillas"},{"05f","Las Lajas"},{"068","Machala"},
            {"06b","Marcabelí"},{"08b","Pasaje"},{"097","Piñas"},{"099","Portovelo"},{"0c2","Santa Rosa"},{"0e0","Zaruma"},

            {"00a","Atacames"},{"03e","Eloy Alfaro"},{"040","Esmeraldas"},{"058","La Concordia"},{"076","Muisne"},{"0a6","Quinindé"},{"0aa","Río Verde"},{"0b5","San Lorenzo"},

            {"052","Isabela"},{"0b1","San Cristóbal"},{"0be","Santa Cruz"},

            {"004","Alfredo Baquerizo Moreno"},{"00f","Balao"},{"011","Balzar"},{"02b","Colimes"},{"02e","Coronel Marcelino Maridueña"},{"032","Daule"},{"034","Durán"},
            {"03d","El Triunfo"},{"03f","El Empalme"},{"045","General Antonio Elizalde"},{"04e","Guayaquil"},{"053","Isidro Ayora"},{"065","Lomas de Sargentillo"},{"06e","Milagro"},
            {"079","Naranjal"},{"07a","Naranjito"},{"07b","Nobol"},{"085","Palestina"},{"090","Pedro Carbo"},{"098","Playas"},{"0af","Salitre"},{"0b0","Samborondón"},
            {"0b3","San Jacinto de Yaguachi"},{"0c1","Santa Lucía"},{"0cc","Simón Bolívar"},

            {"006","Antonio Ante"},{"04d","Cotacachi"},{"051","Ibarra"},{"080","Otavalo"},{"095","Pimampiro"},{"0b8","San Miguel de Urcuquí "},

            {"01e","Cayambe"},{"06c","Mejía"},{"091","Pedro Moncayo"},{"092","Pedro Vicente Maldonado"},{"09e","Puerto Quito"},{"0a8","Quito"},{"0ac","Rumiñahui"},{"0b7","San Miguel de Los Bancos"},

            {"018","Calvas"},{"01d","Catamayo"},{"01f","Celica"},{"022","Chaguarpamba"},{"042","Espíndola"},{"047","Gonzanamá"},{"064","Loja"},{"067","Macará"},{"07d","Olmedo"},
            {"088","Paltas"},{"096","Pindal"},{"0a1","Puyango"},{"0a5","Quilanga"},{"0c7","Saraguro"},{"0cd","Sozoranga"},{"0df","Zapotillo"},

            {"00d","Baba"},{"00e","Babahoyo"},{"016","Buena Fé"},{"070","Mocache"},{"072","Montalvo"},{"084","Palenque"},{"09c","Pueblo Viejo"},{"0a3","Quevedo"},
            {"0a7","Quinsaloma"},{"0d8","Urdaneta"},{"0d9","Valencia"},{"0da","Ventanas"},{"0db","Vinces"},

            {"001","Veinticuatro de Mayo"},{"015","Bolívar"},{"028","Chone"},{"036","El Carmen"},{"043","Flavio Alfaro"},{"054","Jama"},{"055","Jaramijó"},{"056","Jipijapa"},
            {"057","Junín"},{"06a","Manta"},{"073","Montecristi"},{"07c","Olmedo"},{"082","Paján"},{"08f","Pedernales"},{"094","Pichincha"},{"09a","Portoviejo"},
            {"09d","Puerto López"},{"0ab","Rocafuerte"},{"0bb","San Vicente"},{"0bc","Santa Ana"},{"0ce","Sucre"},{"0d6","Tosagua"},

            {"04a","Gualaquiza"},{"04f","Huamboya"},{"062","Limón Indanza"},{"063","Logroño"},{"075","Morona"},{"081","Pablo Sexto"},{"087","Palora"},{"0b4","San Juan Bosco"},
            {"0c3","Santiago de Méndez"},{"0cf","Sucúa"},{"0d2","Taisha"},{"0d5","Tiwintza"},

            {"000","Nacional"},

            {"008","Archidona"},{"01b","Carlos Julio Arosemena Tola"},{"037","El Chaco"},{"0a4","Quijos"},{"0d3","Tena"},

            {"002","Aguarico"},{"059","La Joya de los Sachas"},{"066","Loreto"},{"07f","Francisco de Orellana"},

            {"007","Arajuno"},{"06d","Mera"},{"08c","Pastaza"},{"0bd","Santa Clara"},

            {"05a","La Libertad"},{"0ae","Salinas"},{"0bf","Santa Elena"},

            {"0c5","Santo Domingo"},

            {"01c","Cascales"},{"031","Cuyabeno"},{"046","Gonzalo Pizarro"},{"05d","Lago Agrio"},{"0a0","Putumayo"},{"0c9","Shushufindi"},{"0d0","Sucumbíos"},

            {"005","Ambato"},{"012","Baños de Agua Santa"},{"021","Cevallos"},{"071","Mocha"},{"08d","Patate"},{"0a2","Quero"},{"0ba","San Pedro de Pelileo"},
            {"0c4","Santiago de Píllaro"},{"0d4","Tisaleo"},

            {"020","Centinela del Cóndor"},{"027","Chinchipe"},{"03a","El Pangui"},{"078","Nangaritza"},{"083","Palanda"},{"08a","Paquisha"},{"0dc","Yacuambi"},
            {"0dd","Yantzaza"},{"0de","Zamora"},

            {"03b","El Piedrero"},{"05e","Las Golondrinas"},{"069","Manga del Cura"}
    };

    private String area_of_Peru[][] = {
            {"001","Amazonas"}, {"002","Ancash"},{"003","Apurímac"},{"004","Arequipa"},{"005","Ayacucho"},{"006","Cajamarca"},{"007","Cusco"},
            {"008","Huancavelica"},{"009"," Huánuco "},{"00a","Ica"},{"00b","Junín"},{"00c","La Libertad"},{"00d","Lambayeque"},{"00e","Lima"},{"00f","Callao"},{"010","Loreto"},
            {"011","Madre de Dios"},{"012","Moquegua"},{"013","Pasco"},{"014","Piura"},{"015","Puno"},{"016","San Martín"},{"017","Tacna"},{"018","Tumbes"},{"019","Ucayali"},

            {"020","Chachapoyas"},{"040","Bagua"},{"050","Bongará"},{"060","Condorcanqui"},{"070","Luya"},{"090","Rodríguez de Mendoza"},
            {"0a0","Utcubamba"},{"0c0","Huaraz"},{"0d0","Aija"},{"0e0","Antonio Raymondi"},{"0f0","Asunción"},

            {"100","Bolognesi"},{"120","Carhuaz"},{"130","Carlos Fermín Fitzcarrald"},{"140","Casma"},{"150","Corongo"},{"160","Huari"},
            {"180","Huarmey"},{"190","Huaylas"},{"1a0","Mariscal Luzuriaga"},{"1b0","Ocros"},{"1c0","Pallasca"},{"1d0","Pomabamba"},{"1e0","Recuay"},{"1f0","Santa"},

            {"200","Sihuas"},{"210","Yungay"},{"220","Abancay"},{"230","Andahuaylas"},{"250","Antabamba"},{"260","Aymaraes"},
            {"280","Cotabambas"},{"290","Chincheros"},{"2a0","Grau"},{"2c0","Arequipa"},{"2d0","Camaná"},{"2f0","Caravelí"},

            {"300","Castilla"},{"310","Caylloma"},{"330","Condesuyos"},{"340","Islay"},{"350","La Unión"},{"370","Huamanga"},{"390","Cangallo"},
            {"3a0","Huanca Sancos"},{"3b0","Huanta"},{"3c0","La Mar"},{"3d0","Lucanas"},{"3f0","Parinacochas"},

            {"400","Páucar del Sara Sara"},{"410","Sucre"},{"420","Víctor Fajardo"},{"430","Vilcas Huamán"},{"450","Cajamarca"},{"460","Cajabamba"},
            {"470","Celendín"},{"480","Chota"},{"4a0","Contumazá"},{"4b0","Cutervo"},{"4d0","Hualgayoc"},{"4e0","Jaén"},{"4f0","San Ignacio"},

            {"500","San Marcos"},{"510","San Miguel"},{"520","San Pablo"},{"530","Santa Cruz"},{"550","Cusco"},{"560","Acomayo"},{"570","Anta"},
            {"580","Calca"},{"590","Canas"},{"5a0","Canchis"},{"5b0","Chumbivilcas"},{"5c0","Espinar"},{"5d0","La Convención"},{"5e0","Paruro"},{"5f0","Paucartambo"},

            {"600","Quispicanchi"},{"610","Urubamba"},{"630","Huancavelica"},{"650","Acobamba"},{"660","Angaraes"},{"670","Castrovirreyna"},{"680","Churcampa"},
            {"690","Huaytará"},{"6b0","Tayacaja"},{"6e0","Huánuco"},{"6f0","Ambo"},

            {"700","Dos de Mayo"},{"710","Huacaybamba"},{"720","Huamalíes"},{"730","Leoncio Prado"},{"740","Marañón"},{"750","Pachitea"},{"760","Puerto Inca"},
            {"770","Lauricocha"},{"780","Yarowilca"},{"7a0","Ica"},{"7b0","Chincha"},{"7c0","Nazca"},{"7d0","Palpa"},{"7e0","Pisco"},

            {"800","Huancayo"},{"820","Concepción"},{"840","Chanchamayo"},{"850","Jauja"},{"880","Junín"},{"890","Satipo"},{"8a0","Tarma"},{"8b0","Yauli"},
            {"8c0","Chupaca"},{"8e0","Trujillo"},{"8f0","Ascope"},

            {"900","Bolívar"},{"910","Chepén"},{"920","Julcán"},{"930","Otuzco"},{"940","Pacasmayo"},{"950","Pataz"},{"960","Sánchez Carrión"},{"970","Santiago de Chuco"},
            {"980","Gran Chimú"},{"990","Virú"},{"9b0","Chiclayo"},{"9d0","Ferreñafe"},{"9e0","Lambayeque"},

            {"a00","Lima"},{"a30","Barranca"},{"a40","Cajatambo"},{"a50","Canta"},{"a60","Cañete"},{"a80","Huaral"},{"a90","Huarochirí"},{"ac0","Huaura"},{"ad0","Oyón"},{"ae0","Yauyos"},

            {"b20","Callao"},{"b30", "Maynas"},{"b40","Alto Amazonas"},{"b50","Loreto"},{"b60","Mariscal Ramón Castilla"},{"b70","Requena"},{"b80","Ucayali"},{"b90","Datem del Marañón"},
            {"bb0","Tambopata"},{"bc0","Manú"},{"bd0","Tahuamanu"},{"bf0","Mariscal Nieto"},

            {"c00","General Sánchez Cerro"},{"c10","Ilo"},{"c30","Pasco"},{"c40","Daniel Alcídes Carrión"},{"c50","Oxapampa"},{"c70","Piura"},{"c80","Ayabaca"},{"c90","Huancabamba"},
            {"ca0","Morropón"},{"cb0","Paita"},{"cc0","Sullana"},{"cd0","Talara"},{"ce0","Sechura"},

            {"d00","Puno"},{"d20","Azángaro"},{"d40","Carabaya"},{"d50","Chucuito"},{"d60","El Collao"},{"d70","Huancané"},{"d80","Lampa"},{"d90","Melgar"},{"da0","Moho"},
            {"db0","San Antonio de Putina"},{"dc0","San Román"},{"dd0","Sandia"},{"de0","Yunguyo"},

            {"e00","Moyobamba"},{"e10","Bellavista"},{"e20","El Dorado"},{"e30","Huallaga"},{"e40","Lamas"},{"e50","Mariscal Cáceres"},{"e60","Picota"},{"e70","Rioja"},{"e80","San Martín"},
            {"e90","Tocache"},{"eb0","Tacna"},{"ec0","Candarave"},{"ed0","Jorge Basadre"},{"ee0","Tarata"},

            {"f00","Tumbes"},{"f10","Contralmirante Villar"},{"f20","Zarumilla"},{"f40","Coronel Portillo"},{"f50","Atalaya"},{"f60","Padre Abad"},{"f70","Purús"}

    };

  /*  public Locale_of_EWBS(int _contryIdx, String _countryName, int _areaCode, String _localeArea) {
        super();
        this.countryIndex = _contryIdx;
        this.countryName = _countryName;
        this.areaCode = _areaCode;
        this.localeArea = _localeArea;
    }

    public int getCountryindex() {   return countryIndex;   }

    public void setCountryIndex(int index) {    this.countryIndex = index;    }

    public String getCountryName() {    return countryName;    }

    public void setCountryName(String name) {   this.countryName = name;  }

    public int getAreaCode() { return areaCode; }

    public void setAreaCode(int areaCode) { this.areaCode = areaCode; }

    public String getLocaleAreaName() {    return localeArea;    }

    public void setLocaleAreaName(String localeName) {   this.localeArea = localeName;  }
*/
    public String findArea(int country, int areacnt, String area ){
        String findLocale = "unknown";
        int cnt;
        //TVlog.i("justin", " EWBS country is " + country + "  Area is "+ area);
        switch (country){
            case 0:		//"Argentina":
                findLocale = "unknown";
                break;
            case 1:		//"Belize":
                findLocale = "unknown";
                break;
            case 2:		//"Bolivia":
                findLocale = "unknown";
                break;
            case 3:		//"Botswana":
                findLocale = "unknown";
                break;
            case 4:		//"Brazil":
                if(areacnt != 0 ){
                    for(cnt=0; cnt<area_of_Brazil.length; cnt++){
                        if(area_of_Brazil[cnt][0].equals(area)){
                            findLocale = area_of_Brazil[cnt][1];
                            break;
                        }
                    }
                }
                else{
                    findLocale = "unknown";
                }
                break;
            case 5:		//"Chile":
                findLocale = "unknown";
                break;
            case 6:		//"Costa Rica":
                if(areacnt != 0 ){
                    for(cnt=0; cnt<area_of_CostaRica.length; cnt++){
                        if(area_of_CostaRica[cnt][0].equals(area)){
                            findLocale = area_of_CostaRica[cnt][1];
                            break;
                        }
                    }
                }
                else{
                    findLocale = "unknown";
                }
                break;
            case 7:		//"Ecuador":
                if(areacnt != 0 ){
                    for(cnt=0; cnt<area_of_Ecuador.length; cnt++){
                        if(area_of_Ecuador[cnt][0].equals(area)){
                            findLocale = area_of_Ecuador[cnt][1];
                            break;
                        }
                    }
                }
                else{
                    findLocale = "unknown";
                }
                break;
            case 8:		//"Guatemala":
                findLocale = "unknown";
                break;
            case 9:		//"Honduras":
                findLocale = "unknown";
                break;
            case 10:		//"Japan":
                if(areacnt != 0 ){
                    for(cnt=0; cnt<area_of_Japan.length; cnt++){
                        if(area_of_Japan[cnt][0].equals(area)){
                            findLocale = area_of_Japan[cnt][1];
                            break;
                        }
                    }
                }
                else{
                    findLocale = "unknown";
                }
                break;
            case 11:		//"Maldives":
                findLocale = "unknown";
                break;
            case 12:		//"Nicaragua":
                findLocale = "unknown";
                break;
            case 13:		//"Paraguay":
                findLocale = "unknown";
                break;
            case 14:		//"Peru":
				findLocale = "unknown";
                break;
            case 15:		//"Philippines":
                if(areacnt != 0 ){
                    for(cnt=0; cnt<area_of_Philippines.length; cnt++){
                        if(area_of_Philippines[cnt][0].equals(area)){
                            findLocale = area_of_Philippines[cnt][1];
                            break;
                        }
                    }
                }
                else{
                    findLocale = "unknown";
                }
                break;
            case 16:		//"Sri Lanka":
                findLocale = "unknown";
                break;
            case 17:		//"Uruguay":
                findLocale = "unknown";
                break;
            case 18:	//"Venezuela":
                findLocale = "unknown";
                break;

        }

        return findLocale;
    }
}
