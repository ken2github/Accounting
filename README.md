# Accounting
Family accounting

# Version 1.0
Have a tool able to
- read cvs files corresponding to banck reports for a given year
- check some constraints on provided files, like: balance per count
- build reports containing summary of counts


# Dump DB
In order to dump DB, you run CMD shell and 
- locate into schamas\dumps folder 
- execute "mysql -u username -p dbname < dump-2020-02-15.sql"

# Restore DB or create TEST clone
Create clone for test 
- create copy of original dump sql file replacing inside database name with a different one (may be just suffixing with '-test')
- mysql -u root -p balance_1.6.0-test.db < dump-2020-02-05-TEST.sql