using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Collections;
using System.Text.Json.Serialization;

namespace Server.Entities
{
    public class Album
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        public string Name { get; set; }

        [ForeignKey("Backup")]
        public int BackupId { get; set; }

        [JsonIgnore]
        public virtual Backup? Backup { get; set; }

        public virtual ICollection<Photo>? Photos { get; set; }
    }
}
