-- V37__Add_Last_Checkin_Date_To_Loyalty_Points.sql
ALTER TABLE loyalty_points
    ADD COLUMN last_checkin_date DATE;
