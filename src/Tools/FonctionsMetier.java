/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;


import Entity.Employe;
import Entity.Rayon;
import Entity.Secteur;
import Entity.Travailler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbuffeteau
 */
public class FonctionsMetier
{
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection cnx;

    public FonctionsMetier()
    {
        cnx = ConnexionBDD.getCnx();
    }
    
    public ArrayList<Secteur> GetAllSecteurs()
    {
        ArrayList<Secteur> lesSecteurs = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT numS, nomS FROM secteur");
            rs = ps.executeQuery();
            while(rs.next()){
                Secteur s = new Secteur(rs.getInt("numS"), rs.getString("nomS"));
                lesSecteurs.add(s);
            }
            return lesSecteurs;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lesSecteurs;
    }
    
    public ArrayList<Employe> GetAllEmployes()
    {
        ArrayList<Employe> lesEmployes = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT numE, prenomE FROM employe");
            rs = ps.executeQuery();
            while(rs.next()){
                Employe e = new Employe(rs.getInt("numE"), rs.getString("prenomE"));
                lesEmployes.add(e);
            }
            return lesEmployes;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lesEmployes;
    }
    
    public ArrayList<Rayon> GetAllRayonsByIdsecteur(int numSecteur)
    {
        ArrayList<Rayon> lesRayons = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT numR, nomR FROM rayon WHERE numSecteur = "+numSecteur);
            rs = ps.executeQuery();
            while(rs.next()){
                Rayon r = new Rayon(rs.getInt("numR"), rs.getString("nomR"));
                lesRayons.add(r);
            }
            return lesRayons;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lesRayons;
    }
    
    public ArrayList<Travailler> GetAllTravailler(int numRayon)
    {
        ArrayList<Travailler> lesTravaillers = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT codeE, codeR, date, temps, employe.prenomE FROM travailler,employe WHERE codeR = "+numRayon+" AND employe.numE = codeE");
            rs = ps.executeQuery();
            while(rs.next()){
                Employe e = new Employe(rs.getInt("CodeE"), rs.getString("prenomE"));
                Travailler t = new Travailler(e, rs.getString("date"), rs.getInt("temps"));
                lesTravaillers.add(t);
            }
            return lesTravaillers;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lesTravaillers;
    }
    
    public int GetIdEmployeByNom(String nomEmploye)
    {
        try{
            ps = cnx.prepareStatement("SELECT numE FROM employe WHERE prenomE = '"+nomEmploye+"'");
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("numE");
            }
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int TotalHeuresRayon(int numRayon)
    {
        try{
            ps = cnx.prepareStatement("SELECT SUM(temps) FROM travailler WHERE codeR = "+numRayon);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public void ModifierTemps(int codeEmploye, int CodeRayon, String uneDate,int nouveauTemps)
    {
        try{
            ps = cnx.prepareStatement("UPDATE travailler SET temps = "+nouveauTemps+" WHERE codeE = "+codeEmploye+" AND codeR = "+CodeRayon+" AND date ='"+uneDate+"'");
            ps.executeUpdate();
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void InsererTemps(int codeEmploye, int CodeRayon,int nouveauTemps)
    {
        try{
            LocalDate ajrd = java.time.LocalDate.now();
            ps = cnx.prepareStatement("INSERT INTO travailler (codeE,CodeR,date,temps) VALUES("+codeEmploye+","+CodeRayon+",'"+ajrd+"',"+nouveauTemps+")");
            ps.executeUpdate();
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean TestTravailler(int codeEmploye, int CodeRayon){
        try{
            LocalDate ajrd = java.time.LocalDate.now();
            ps = cnx.prepareStatement("SELECT temps FROM travailler WHERE codeE = "+codeEmploye+" AND codeR = "+CodeRayon+" AND date = '"+ajrd+"'");
            rs = ps.executeQuery();
            if(rs.next()){
                return false;
            }
            else{
                return true;
            }
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
