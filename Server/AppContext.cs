using Microsoft.EntityFrameworkCore;
using Server.Entities;

namespace Server
{
    public class AppContext : DbContext
    {
        public DbSet<Photo> Photos { get; set; }
        public DbSet<Album> Albums { get; set; }
        public DbSet<Backup> Backups { get; set; }

        public AppContext() : base()
        {
            Database.EnsureCreated();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            var config = new ConfigurationBuilder()
                .AddJsonFile("appsettings.json")
                .SetBasePath(Directory.GetCurrentDirectory())
                .Build();

            optionsBuilder
                .UseSqlite(config.GetConnectionString("DefaultConnection"));
        }
    }
}
