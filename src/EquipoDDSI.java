
import static java.lang.System.exit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class EquipoDDSI {

 
    private static void MostrarCalendario() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/club", "root", "perro");
            if (conn != null) {

                try (Statement st = conn.createStatement()) {
                    Scanner sc = new Scanner(System.in);
                    
                    System.out.println("Introduza su nombre:");                    
                    String nombre = sc.nextLine();
                    String query = "select Fecha, Hora, Nombre, Tipo from Instalacion NATURAL JOIN GrupoReducido where IdGrupo"
                            + " in(select Jugador_GrupoReducido.IdGrupo from Jugador_GrupoReducido INNER JOIN Jugador ON "
                            + "Jugador_GrupoReducido.IdJugador = Jugador.IdJugador AND Jugador.Nombre='"+nombre+"');";
                    ResultSet rs = st.executeQuery(query);                    
                    while (rs.next()) {
                        String salida = rs.getString("Fecha");
                        System.out.format("Tienes entrenamiento el día %s", salida);
                        salida = rs.getString("Hora");
                        System.out.format(" a las %s", salida);
                        salida = rs.getString("Nombre");
                        System.out.format(" en la instalación: %s", salida);
                        salida = rs.getString("Tipo");
                        System.out.format(" del tipo: %s \n", salida);
                                               
                    }
                    
                    
                    
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
   
   
   
    
    private static void ListarPlantillaTitular() {
        
        try {
            
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/club", "root", "perro");
            if (conn != null) {

                try (Statement st = conn.createStatement()) {
                    
                    String query = "SELECT Nombre, Posicion FROM Jugador WHERE Titular=1;";
                    ResultSet rs = st.executeQuery(query);                    
                    while (rs.next()) {
                        
                        String nombre = rs.getString("Nombre");
                        String pos = rs.getString("Posicion");
                        System.out.format("\t%s: %s\n", pos, nombre);
                    }
                    
                } catch (SQLException ex) {
                    
                    System.err.println(ex.getMessage());
                }
            }

        } catch (SQLException ex) {
            
            System.err.println(ex.getMessage());
        }
    }
    
    
    
    
    private static void ConsultarVentasPorJugador() {
        
        try {
            
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/club", "root", "perro");
            if (conn != null) {              
                
                try (Statement st = conn.createStatement()) {
                    
                    String query = null;
                    System.out.print("1: Consultar el total de artículos vendidos de cada jugador.\n");
                    System.out.print("2: Consultar el total de artículos vendidos de un jugador.\n");
                    System.out.print("Escoja una opcion: ");
                    Scanner sc = new Scanner(System.in);
                    int action = sc.nextInt();
                    switch (action) {
                        
                        case 1: 
                            query = "SELECT Jugador.Nombre, sum(Vendidos) FROM Producto INNER JOIN Jugador ON Producto.IdJugador=Jugador.IdJugador GROUP BY Producto.IdJugador;";
                            break;
                        case 2:
                            System.out.print("\tIntroduzca el nombre: ");
                            sc = new Scanner(System.in);
                            String name = sc.nextLine();
                            query = "SELECT Jugador.Nombre, sum(Vendidos) FROM Producto INNER JOIN Jugador ON Producto.IdJugador =Jugador.IdJugador AND Jugador.Nombre='" + name +"';";
                            break;
                    }
                    ResultSet rs = st.executeQuery(query);                    
                    while (rs.next()) {
                        
                        String nombre = rs.getString("Nombre");
                        String vendidos = rs.getString("sum(Vendidos)");
                        System.out.format("\t%s: %s\n", nombre, vendidos);
                    }
                    
                } catch (SQLException ex) {
                    
                    System.err.println(ex.getMessage());
                }
            }
            
        } catch (SQLException ex) {
            
            System.err.println(ex.getMessage());
        }
    }
    
    
    
    
    private static void ComprarEntrada() {
        
        try {
            
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/club", "root", "perro");
            if (conn != null) {
                
                try (Statement st = conn.createStatement()) {
                    
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Fechas de los próximos partidos: ");
                    String query = "SELECT Fecha FROM FichaAsientos;";
                    ResultSet rs = st.executeQuery(query);                    
                    while (rs.next()) {
                        
                        String salida = rs.getString("Fecha");
                        System.out.format("%s\n", salida);
                    }
                    System.out.println("Introduce la fecha del partido al que quieres asistir: ");
                    String fecha = sc.nextLine();
                    System.out.println("Introduce tu dni: ");
                    sc = new Scanner(System.in);
                    String dni = sc.nextLine();
                    System.out.println("Introduce la grada donde quieres tu entrada (FondoNorte, FondoSur, Preferente, Tribuna): ");
                    sc = new Scanner(System.in);
                    String grada = sc.nextLine();
                    query = "SELECT " + grada + " FROM FichaAsientos where Fecha='" + fecha +"';";
                    rs = st.executeQuery(query);
                    int disponibles = 0;
                    while (rs.next()) {
                        
                        disponibles = rs.getInt(grada);
                    }
                    
                    if (disponibles == 0){
                        
                        System.out.println("No quedan entradas disponibles en esa grada");
                    
                    } else {
                        
                        disponibles -= 1;
                        query = "Insert ignore into Aficionado(DNI) values ('"+dni+"');";
                        rs = st.executeQuery(query);
                        query = "UPDATE FichaAsientos SET " + grada + " = " + disponibles + " WHERE Fecha='" + fecha +"';";
                        rs = st.executeQuery(query);
                        query = "Insert into Entrada(Fecha,Grada,Precio,Descuento,DNI)"
                                + " values('"+fecha+"','"+grada+"',50"+",0,'"+dni+"');";
                        rs = st.executeQuery(query);
                        System.out.println("Compra realizada");
                    }
                    
                } catch (SQLException ex) {
                    
                    System.err.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            
            System.err.println(ex.getMessage());
        }
    }
    
    
    
    public static void main(String[] args) {
        
        while(true){
            // Menú
            System.out.print("\n\n\nEstas son las funcionalidades disponibles:\n");
            System.out.print("1: Listar plantilla titular.\n");
            System.out.print("2: Comprar entrada.\n");
            System.out.print("3: Consultar ventas por jugador.\n");
            System.out.print("4: Mostrar calendario de entrenamientos.\n");
            System.out.print("0: Salir.\n");

            System.out.print("Indique el numero de la actividad que desea realizar: ");
            Scanner sc = new Scanner(System.in);
            int funcionality = sc.nextInt();
            switch(funcionality) {

                case 0: exit(0); break;
                case 1: ListarPlantillaTitular(); break;
                case 2: ComprarEntrada(); break;
                case 3: ConsultarVentasPorJugador(); break;
                case 4: MostrarCalendario(); break;
            }
        }
    }
    
      
}
